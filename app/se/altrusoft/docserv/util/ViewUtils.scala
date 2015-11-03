package se.altrusoft.docserv.util

import java.text.SimpleDateFormat
import java.text.DateFormat
import java.util.Date

object ViewUtils {
  
	def formatDate(millis: Long) : String = {
		new SimpleDateFormat().format(new Date(millis));
	}
	
}