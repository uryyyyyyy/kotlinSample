package example

import dto.OwnCloudFile
import org.apache.commons.httpclient.Credentials
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.DeleteMethod
import org.apache.commons.httpclient.methods.FileRequestEntity
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.PutMethod
import org.apache.jackrabbit.webdav.DavConstants
import org.apache.jackrabbit.webdav.MultiStatusResponse
import org.apache.jackrabbit.webdav.client.methods.DavMethod
import org.apache.jackrabbit.webdav.client.methods.MkColMethod
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.ArrayList

object OwnCloudDao {

    /**
     * ファイルをダウンロードする。重いファイルでもメモリを食いつぶさないはず。
     * 見つからなければ例外を返す。
     * @param host
     * @param user
     * @param pass
     * @param targetPath
     * @return
     */
    fun getFile(host:String, user:String, pass:String, targetPath:String):File {
        try{
            val urlString = "http://$host/remote.php/webdav/$targetPath"
            val client = HttpClient()
            val creds = UsernamePasswordCredentials(user, pass)
            client.getState().setCredentials(AuthScope.ANY, creds)
            val getM = GetMethod(urlString)
            client.executeMethod(getM)
            val is_ = getM.getResponseBodyAsStream()
            val file = File.createTempFile("pre", "suf")
            Files.copy(is_, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            return file
        }catch(e:Exception){
            println("error")
            throw IOException(e)
        }
    }

    /**
     * fileをuploadする。親フォルダがない場合は400系を返す。
     * 既にファイルがあれば上書きする。(owncloud内部でバージョニングされる。)
     * @param host
     * @param user
     * @param pass
     * @param targetFolder
     * @param file
     * @return
     */
    fun postFile(host:String, user:String, pass:String, targetFolder:String, file:File):Int{
        try{
            val urlString = "http://${host}/remote.php/webdav/${targetFolder}"
            val client = HttpClient()
            val creds:Credentials = UsernamePasswordCredentials(user, pass)
            client.getState().setCredentials(AuthScope.ANY, creds)
            val post:PutMethod = PutMethod(urlString)

            //val parts:Array<Part> = array(FilePart(file.getName(), file))
            val type = Files.probeContentType(file.toPath())
            post.setRequestEntity(FileRequestEntity(file, type))
            return client.executeMethod(post)
        }catch(e:Exception){
            println("error")
            throw IOException(e)
        }
    }

    /**
     * フォルダを作成する。親フォルダが存在しない時は404を返す。
     * @param host
     * @param user
     * @param pass
     * @param targetFolder
     * @return statusCode
     */
    fun createFolder(host:String, user:String, pass:String, targetFolder:String):Int{
        try{
            val urlString = "http://${host}/remote.php/webdav/${targetFolder}"
            val client = HttpClient()
            val creds = UsernamePasswordCredentials(user, pass)
            client.getState().setCredentials(AuthScope.ANY, creds)
            val pFind:DavMethod = MkColMethod(urlString);
            return client.executeMethod(pFind);
        }catch(e:Exception){
            println("error")
            throw IOException(e)
        }
    }

    /**
     *
     * targetFolder内にあるファイルの一覧を表示する。が、Name以外の値はとれないっぽい。
     * (子階層は表示しない。もしニーズがあれば別APIを追加する。)
     * @throws IOException そんなフォルダはない。他ネットワークエラー
     * @param host
     * @param user
     * @param pass
     * @param targetFolder
     * @return Fileの概要。といってもNameだけ。
    *
     */
    fun getList(host:String, user:String, pass:String, targetFolder:String):List<OwnCloudFile>{
        try{
            val urlString = "http://${host}/remote.php/webdav/${targetFolder}"
            val client = HttpClient()
            val creds = UsernamePasswordCredentials(user, pass)
            client.getState().setCredentials(AuthScope.ANY, creds)

            val pFind:DavMethod = PropFindMethod(urlString, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1)
            client.executeMethod(pFind)
            val multiStatus = pFind.getResponseBodyAsMultiStatus()
            val results = ArrayList<OwnCloudFile>()
            for (res:MultiStatusResponse in multiStatus.getResponses()) {
                val name = res.getHref().replace("/remote.php/webdav/${targetFolder}", "")
                if(name.isEmpty()) continue

                var contentLength:Long = 0
                var lastModified:String? = null
                var contentType:String? = null
                var id:String? = null
                for(i in res.getProperties(200)){
                    val key = i.getName().getName()
                    if(key == "getcontentlength"){
                        contentLength = i.getValue().toString().toLong()
                    }else if(key == "getlastmodified"){
                        lastModified = i.getValue().toString()
                    }else if(key == "getcontenttype"){
                        contentType = i.getValue().toString()
                    }else if(key == "id"){
                        id = i.getValue().toString()
                    }
                }
                results.add(OwnCloudFile(id, name, contentType, lastModified, contentLength))
            }
            return results
        }catch(e:Exception){
            println("error")
            throw IOException(e)
        }
    }

    /**
     *
     * targetPathにあるファイルを削除する。なければ404を返す
     * @param host
     * @param user
     * @param pass
     * @param targetPath
     * @return statusCode
    *
     */
    fun deleteFile(host:String, user:String, pass:String, targetPath:String):Int{
        try{
            val urlString = "http://${host}/remote.php/webdav/${targetPath}"
            val client = HttpClient()
            val creds = UsernamePasswordCredentials(user, pass)
            client.getState().setCredentials(AuthScope.ANY, creds)
            val deleteM = DeleteMethod(urlString)
            //DavMethod pFind = new PropFindMethod(urlString, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1)
            //client.executeMethod(pFind)
            return client.executeMethod(deleteM)
        }catch(e:Exception){
            println("error")
            throw IOException(e)
        }
    }

}