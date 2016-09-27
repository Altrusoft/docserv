//import com.typesafe.sbt.SbtNativePackager._
//import com.typesafe.sbt.packager.Keys._

import com.typesafe.config._

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

name    := conf.getString("app.name")

version := conf.getString("app.version")

organization := "se.altrusoft"

lazy val root = (project in file(".")).enablePlugins(PlayJava, RpmPlugin)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	"org.slf4j" % "slf4j-api" % "1.7.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.core" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.document" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.template" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.document.odt" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.document.ods" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.template.velocity" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.template.freemarker" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.converter" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.converter.odt.odfdom" % "1.0.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.itext.extension" % "1.0.5",
	//"org.springframework" % "spring-context" % "3.2.4.RELEASE",
	"commons-io" % "commons-io" % "2.4",
	"commons-collections" % "commons-collections" % "3.2.1",
	"commons-lang" % "commons-lang" % "2.4",
	"commons-configuration" % "commons-configuration" % "1.8",
	"oro" % "oro" % "2.0.8",
	"com.lowagie" % "itext" % "2.1.7",
	"org.odftoolkit" % "odfdom-java" % "0.8.7",
	"xerces" % "xercesImpl" % "2.9.1",
	"xml-apis" % "xml-apis" % "1.3.04",
	"joda-time" % "joda-time" % "2.1",
	"com.fasterxml.jackson.module" % "jackson-module-mrbean" % "2.1.2",
	"cglib" % "cglib" % "2.2.2",
	"commons-beanutils" % "commons-beanutils" % "1.8.3",
	// json-lib seems to be missing
	//"net.sf.json-lib" % "json-lib" % "2.4",
	"net.sf.corn" % "corn-cps" % "1.1.7",
	// used only during test for verification
	"com.itextpdf" % "itextpdf"  % "5.4.0" % "test"
)

def javaVersion: String = {
	val ver = System.getenv("DOCSERV_JAVA_VERSION");
	if (ver == null) {
		"1.8";
	} else {
		ver;
	}
}

javacOptions ++= Seq("-source", javaVersion, "-target", javaVersion)

//play.Project.playJavaSettings

seq(sonar.settings :_*)

//packageArchetype.java_server

packageDescription in Linux := "Docserv generates reports from Json data using xdocreport and libreoffice"

packageSummary in Linux := "Docserv is a report generator"

maintainer in Debian := "Altrusoft AB <info@altrusoft.se>"

maintainer in Linux := "Altrusoft AB <info@altrusoft.se>"

rpmRelease := "1"

rpmVendor := "altrusoft.se"

rpmVendor in Rpm := "altrusoft.se"

rpmUrl := Some("http://github.com/example/server")

rpmLicense := Some("Apache v2")

publishTo := Some(Resolver.file("file",  new File( "target/repo" )) )

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)
