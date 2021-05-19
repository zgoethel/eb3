package net.jibini.eb

import java.io.File

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import org.slf4j.LoggerFactory

import java.util.*

/**
 * Core configuration for the indexing and querying engines; this
 * file allows configuration of security, performance, and usability
 * features.
 *
 * @author Zach Goethel
 */
class EasyButtonConfig
{
	/**
	 * Disables validation of the REST APIs' certificates; this is
	 * necessary as some SSL certificates are neglected or self-signed.
	 */
	val disableCertCheck = false
	
	/**
	 * Disables the marketing front page and loads the login page
	 * immediately.
	 */
	val defaultToLoginPage = false

	/**
	 * The simple class name of the system's authenticator class.
	 */
	val primaryAuthenticator = "DumbAuthenticator"

	/**
	 * Upon loading the search page, it will default to loading this document's
	 * repository. The document type can be changed per-session in the UI.
	 */
	val defaultSearchDocument = "NONE"

	/**
	 * Floor clients will need to be configured with this secret key in order to
	 * submit new and updated document to the server.
	 *
	 * Do not post this secret publicly. The default secret key is random.
	 */
	val secret = UUID.randomUUID()
		.toString()
		.replace("-", "")
	
	companion object
	{
		private val log = LoggerFactory.getLogger(this::class.java)
		
		/**
		 * Loads the given configuration data class from its respective
		 * JSON file (where the filename is the simple class name and
		 * the JSON file extension).
		 *
		 * If the configuration doesn't exist, the provided default
		 * value will be written to file and returned.
		 *
		 * @return The existing configuration object, or the defaults
		 *     provided if no file already exists.
		 * @param T Configuration object and return type.
		 */
		@JvmStatic
		fun <T : Any> loadOrDefault(default: T) : T
		{
			val path = "${default::class.simpleName}.json"
			
			log.info("Loading configuration file from '$path' . . .")
			
			val configFile = File(path)
			
			if (configFile.exists())
				return Gson().fromJson(configFile.readText(), default::class.java)
			else
			{
				log.info("Creating default configuration for '$path' . . .")
				
				// Enable pretty printing to make it easier to read
				val gson = GsonBuilder()
					.setPrettyPrinting()
					.create()
				
				configFile.createNewFile()
				configFile.writeText(gson.toJson(default))
				
				return default
			}
		}
	}
}