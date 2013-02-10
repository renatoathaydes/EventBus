// script called by the GMaven plugin


import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import static com.google.common.io.Files.copy

def bundlesDir = new File( project.properties.'bundles.dir' )
if ( !bundlesDir.exists() ) bundlesDir.mkdir()

def targetDir = new File( project.build.directory )
if ( !targetDir.exists() ) fail 'Cannot proceed as the target directory does not exist'

// copy jar files in the target directory to bundles directory
def jarFiles = targetDir.listFiles().grep { it.name.endsWith '.jar' }
jarFiles.each { jar ->
	copy jar, new File( bundlesDir, jar.name )
	log.info "Copied ${jar.name} to bundles directory"
}

// warn if any Jars are not actually OSGi bundles in the bundles directory
bundlesDir.listFiles().each { jar ->
	checkIfValidBundle jar
}

def checkIfValidBundle( jar ) {
	ZipInputStream zis = new ZipInputStream( new FileInputStream( jar ) )

	ZipEntry ze
	def hasManifest = false
	try {
		while ( ze = zis.nextEntry ) {
			if ( ze.name == 'META-INF/MANIFEST.MF' ) {
				hasManifest = true
				break;
			}
		}
		if ( !hasManifest ) log.info "No manifest found in ${jar.name}"
		if ( !hasManifest || !isBundle( zis ) )
			log.warn "${jar.name} does not seem to be a valid bundle!"
	} finally {
		if ( zis ) zis.close()
	}
}

def isBundle( inputStream ) {
	def countHeaders = 0
	inputStream.eachLine {
		if ( isMandatoryHeader( it ) ) countHeaders++
	}
	return countHeaders > 1
}

def isMandatoryHeader( line ) {
	line = line.trim().toLowerCase()
	return line.startsWith( 'bundle-symbolicname' ) ||
			line.startsWith( 'bundle-version' )
}