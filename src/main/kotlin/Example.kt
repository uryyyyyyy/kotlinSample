package example

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.jackrabbit.webdav.DavConstants
import org.apache.jackrabbit.webdav.MultiStatusResponse
import org.apache.jackrabbit.webdav.client.methods.DavMethod
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod

fun main(args: Array<String>) {

    println("ownCloud client start!")

    val user = "user"
    val pass = "bitnami"
    val host = "172.27.51.63"

    println("---- get fileList ----")
    val list = OwnCloudDao.getList(host, user, pass, "Photos/")
    list.forEach{v -> println(v)}
    //
    //    println("---- download file ----")
    //    val file = OwnCloudDao.getFile(host, user, pass, "Photos/Paris.jpg")
    //
    //    println("---- upload file ----")
    //    val code = OwnCloudDao.postFile(host, user, pass, "Photos/sss/"+"Paris.jpg", file)
    //    println(code)
    //
    //    println("---- create folder ----")
    //    val code2 = OwnCloudDao.createFolder(host, user, pass, "Photos/sss/ssss/")
    //    println(code2)
    //
    //    println("---- delete file ----")
    //    val code3 = OwnCloudDao.deleteFile(host, user, pass, "Photos/sss/"+"Paris.jpg")
    //    println(code3)
    //
    //    println("---- delete folder ----")
    //    val code4 = OwnCloudDao.deleteFile(host, user, pass, "Photos/sss/ssss/")
    //    println(code4)


    println("---- Option: get fileField ----")
    val targetFolder = "Photos/"
    val urlString = "http://${host}/remote.php/webdav/${targetFolder}"
    val client = HttpClient()
    val creds = UsernamePasswordCredentials(user, pass)
    client.getState().setCredentials(AuthScope.ANY, creds)

    val pFind: DavMethod = PropFindMethod(urlString, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1)
    client.executeMethod(pFind)
    val multiStatus = pFind.getResponseBodyAsMultiStatus()
    for (res: MultiStatusResponse in multiStatus.getResponses()) {
        val name = res.getHref().replace("/remote.php/webdav/${targetFolder}", "")
        if(name.isEmpty()) continue
        for(i in res.getProperties(200)){
            println(i.getName().getName() + " " + i.getValue())
        }
    }
}