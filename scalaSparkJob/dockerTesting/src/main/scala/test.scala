import redis.clients.jedis.Jedis
import java.sql.Connection
import java.sql.Statement
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object test {
  def main(args : Array[String]){

    val conf = new SparkConf().setMaster("local").setAppName("My App")
    val sc = new SparkContext(conf)

    val jedis = new Jedis("redisDB")
    try{
      jedis.connect()
    }catch {
      case e: Exception => {
        e.printStackTrace()
        println("redis connection not established")
      }
    }
    val dbName = "firstdb"
    val JDBC_DRIVER = "com.mysql.jdbc.Driver"
    val DB_URL = "jdbc:mysql://mysqlDB:3306/"+dbName
    println("DB_URL" +DB_URL)
    val USER = "user"
    val PASS = "pass123"

    var conn : Connection= null
    var stmt : Statement= null
    try {
      Class.forName("com.mysql.jdbc.Driver")

      println("Connecting to database...")
      conn = DriverManager.getConnection(DB_URL, USER, PASS)

      stmt = conn.createStatement()
      stmt.executeUpdate("CREATE TABLE Person (PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255) );")
      stmt.executeUpdate("INSERT INTO Person (PersonID, LastName, FirstName, Address, City) VALUES (1, 'Kumar', 'Suresh', 'Jackal Creek','Johannesburg');")
      stmt.executeUpdate("INSERT INTO Person (PersonID, LastName, FirstName, Address, City) VALUES (2, 'Kapoor', 'Deepak', 'Wall road','Steger');")
      stmt.executeUpdate("INSERT INTO Person (PersonID, LastName, FirstName, Address, City) VALUES (3, 'Kumar', 'Rahul', 'JVaishali Nagar','Jaipur');")
      stmt.executeUpdate("INSERT INTO Person (PersonID, LastName, FirstName, Address, City) VALUES (4, 'Stuart', 'Jim', 'Stone Appartments','San Francisco');")
      val sql = "SELECT PersonID, FirstName, LastName, Address, City FROM Person"
      val rs: ResultSet = stmt.executeQuery(sql)

      val colCount = rs.getMetaData.getColumnCount
      val delim = "\t"

      def getRowFromResultSet(resultSet: ResultSet): String ={
        var i:Int = 1
        var rowStr=""
        while(i<=colCount){
          rowStr=rowStr+resultSet.getString(i)+delim
          i+=1
        }
        rowStr
      }

      val resultSetList = Iterator.continually((rs.next(), rs)).takeWhile(_._1).map(r => {
        getRowFromResultSet(r._2) // (ResultSet) => (spark.sql.Row)
      }).toList

      val x = sc.parallelize(resultSetList)
      x.saveAsTextFile("file:/testingX/test1")
      println("saved successfully")

      var i = 1
      while(rs.next()){
        jedis.set("id_"+i,rs.getInt("PersonID").toString)
        jedis.set("first_"+i,rs.getString("FirstName"))
        jedis.set("last_"+i,rs.getString("LastName"))
        jedis.set("address_"+i,rs.getString("Address"))
        jedis.set("city_"+i,rs.getString("City"))

        println("Reading from redis")
        println("ID: " + jedis.get("id_"+i))
        println("First: " + jedis.get("first_"+i))
        println("Last: " + jedis.get("last_"+i))
        println("Address: " + jedis.get("address_"+i))
        println("City: " + jedis.get("city_"+i))
        i = i + 1
      }

      rs.close()
      stmt.close()
      conn.close()
    }catch {
      case se : SQLException => se.printStackTrace()
      case e : Exception => e.printStackTrace()
    }finally{
      try{
        if(stmt!=null)
          stmt.close()
      }catch{
        case se2 : SQLException => se2.printStackTrace()
      }
      try{
        if(conn!=null)
          conn.close()
      }catch{
        case se : SQLException => se.printStackTrace()
      }
    }
  }
}
