package net.jibini.eb

import java.io.File

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import org.slf4j.LoggerFactory

/**
 * Core configuration for the indexing and querying engines; this file allows configuration of security, performance,
 * and usability features.
 *
 * @author Zach Goethel
 */
class EasyButtonConfig
{
	/**
	 * Disables validation of the REST APIs' certificates; this is necessary as some SSL certificates are neglected or
	 * self-signed.
	 */
	val disableCertCheck: Boolean = false
	
	/**
	 * Disables the marketing front page and loads the login page immediately.
	 */
	val defaultToLoginPage: Boolean = false

	/**
	 * The simple class name of the system's authenticator class.
	 */
	val primaryAuthenticator: String = "DumbAuthenticator"
	
	companion object
	{
		private val log = LoggerFactory.getLogger(this::class.java)
		
		/**
		 * Loads the given configuration data class from its respective JSON file (where the filename is the simple
		 * class name and the JSON file extension).
		 *
		 * If the configuration doesn't exist, the provided default value will be written to file and returned.
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