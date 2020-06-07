import io.circe.Json

object Create extends App {

  val jsonFields = Json.fromFields(
    List(
      ("name", Json.fromString("sample json")),
      ("version", Json.fromInt(1)),
      ("data", Json.fromFields(List(
        ("done", Json.fromBoolean(false)),
        ("rate", Json.fromDouble(4.9).get)
      )))
    )
  )

  println(jsonFields.spaces4)
  println(jsonFields.spaces2)
  println(jsonFields.noSpaces)
  println("""{"name":"sample json","version":1,"data":{"done":false,"rate":4.9}}""")
}
