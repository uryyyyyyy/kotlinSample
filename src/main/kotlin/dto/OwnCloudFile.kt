package dto

data class OwnCloudFile(val id:String?,
                        val name:String,
                        val contentType:String?,
                        val lastModified:String?,
                        val contentLength:Long)

