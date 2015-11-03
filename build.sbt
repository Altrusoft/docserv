// DocServ

name := "docserv"

organization := "se.altrusoft"

version := "0.32"

enablePlugins(
	JavaServerAppPackaging,
	BuildInfoPlugin
)

resolvers += "Spring Snapshots" at "http://maven.springframework.org/snapshot"

libraryDependencies ++= Seq(
	javaCore,
	javaWs,
	"junit" % "junit" % "4.12" % "test->default",
	"com.novocode" % "junit-interface" % "0.10" % "test->default",
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
  	// used only during test for verification
	"com.itextpdf" % "itextpdf"  % "5.4.0" % "test"
)

scalaVersion := "2.11.7"

def javaVersion: String = "1.8";

javacOptions ++= Seq("-source", javaVersion, "-target", javaVersion)

javacOptions in compile += "-g"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

routesGenerator := InjectedRoutesGenerator

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Managed

seq(
	sonar.settings :_*
)

buildInfoKeys := Seq[BuildInfoKey](
	name, 
	version, 
	BuildInfoKey.action("buildTime") {
		System.currentTimeMillis
	})

buildInfoPackage := "build"

packageDescription in Linux := "Docserv generates reports from Json data using xdocreport and libreoffice"

packageSummary in Linux := "Docserv is a report generator"

maintainer in Debian := "Altrusoft AB <info@altrusoft.se>"

publishTo := Some(Resolver.file("file",  new File( "target/repo" )) )
