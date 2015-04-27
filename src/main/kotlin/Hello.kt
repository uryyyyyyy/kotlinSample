package example

fun main(args: Array<String>) {

    println("ownCloud client start!")

    val user = "user"
    val pass = "bitnami"
    val host = "172.27.51.21"

    println("---- get fileList ----")
    val list = OwnCloudDao.getList(host, user, pass, "Photos/")
    list.forEach{v -> println(v)}

    println("---- download file ----")
    val file = OwnCloudDao.getFile(host, user, pass, "Photos/Paris.jpg")

    println("---- upload file ----")
    val code = OwnCloudDao.postFile(host, user, pass, "Photos/sss/"+"Paris.jpg", file)
    println(code)

    println("---- create folder ----")
    val code2 = OwnCloudDao.createFolder(host, user, pass, "Photos/sss/ssss/")
    println(code2)

    println("---- delete file ----")
    val code3 = OwnCloudDao.deleteFile(host, user, pass, "Documents/ss")
    println(code3)
}