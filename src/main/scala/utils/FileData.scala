package utils

import dogApi.PhotoApi
import java.io.File

case class FileData(file: Option[File], bytes: Option[Array[Byte]], url: String, api: PhotoApi)