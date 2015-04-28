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
import org.junit.Test as test

class OwnCloudDaoTest {

    test fun myFirstTest() {
        println("aaa")
    }
}