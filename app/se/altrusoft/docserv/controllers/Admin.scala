package se.altrusoft.docserv.controllers

import javax.inject.Inject
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.libs.json.Json
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Message
import play.api.i18n.Langs
import play.api.Logger
import play.api.Play.current
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import play.api.libs.json.JsString
import java.io.FileNotFoundException

class Admin @Inject() (val messagesApi: MessagesApi) (val langs: Langs) extends Controller with I18nSupport {
	
	def index = Action { implicit request =>
		Ok(views.html.index());
	}
	
	def templates = Action { implicit request =>
		val dirName: String = current.configuration.getString("docserv.template.dir").getOrElse("/var/docserv");
		val templates = new File(dirName).listFiles().map((td) => templateData(td)).toList.flatten;
		Ok(views.html.templates(templates));
	}
	
	def version = Action {
		Ok(
			Json.obj(
				"version" -> build.BuildInfo.version
			)
		);
	}
	
	def templateData(templateDir: File) = {
		try {
			val is: InputStream = new FileInputStream(new File(templateDir + File.separator + "template.json"));
			val emptyJsonString = JsString("");
			val templateJson = Json.parse(is);
			Option.apply((
				templateDir.getName, 
				(templateJson \ "name").getOrElse(emptyJsonString).as[JsString].value, 
				(templateJson \ "description").getOrElse(emptyJsonString).as[JsString].value, 
				(templateJson \ "author").getOrElse(emptyJsonString).as[JsString].value
			));
		} catch {
			case fnfe: FileNotFoundException => {
				Option.empty;
			}
		}
	}
	
}