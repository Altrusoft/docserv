import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._

name := "docserv"

version := "0.31"

libraryDependencies ++= Seq(
	"org.slf4j" % "slf4j-api" % "1.7.5",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.core" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.document" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.template" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.document.odt" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.document.ods" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.template.velocity" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.template.freemarker" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.converter" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.converter.odt.odfdom" % "1.0.3",
	"fr.opensagres.xdocreport" % "fr.opensagres.xdocreport.itext.extension" % "1.0.3",
  	"org.springframework" % "spring-context" % "3.2.4.RELEASE",
    "commons-io" % "commons-io" % "1.4",
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
	// used only during test for verification
	"com.itextpdf" % "itextpdf"  % "5.4.0" % "test"
)

play.Project.playJavaSettings

packageArchetype.java_server

packageDescription in Linux := "Docserv generates reports from Json data using xdocreport and libreoffice"

packageSummary in Linux := "Docserv is a report generator"

maintainer in Debian := "Altrusoft AB <info@altrusoft.se>"
