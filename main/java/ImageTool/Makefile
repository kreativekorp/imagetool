SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

all: clean bin doc imagetool.jar imagetool-src.tgz SpriteLoader.jar SpriteLoader.app SpriteLoader.exe

eclipseall: eclipseclean imagetool.jar imagetool-src.tgz SpriteLoader.jar SpriteLoader.app SpriteLoader.exe

clean:
	rm -rf bin
	rm -rf doc
	rm -rf imagetool*.jar
	rm -rf imagetool*.tgz
	rm -rf SpriteLoader.jar
	rm -rf SpriteLoader*.app
	rm -rf SpriteLoader.exe

eclipseclean:
	rm -rf imagetool*.jar
	rm -rf imagetool*.tgz
	rm -rf SpriteLoader.jar
	rm -rf SpriteLoader*.app
	rm -rf SpriteLoader.exe

bin: src
	mkdir -p bin
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -sourcepath src $(PACKAGES) -d doc

imagetool.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF imagetool.jar -C bin com/kreative
	chmod +x imagetool.jar

imagetool-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf imagetool-src.tgz src/com/kreative

SpriteLoader.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST-SL.MF SpriteLoader.jar -C bin com/kreative
	chmod +x SpriteLoader.jar

SpriteLoader.app: SpriteLoader-Pre10.15.app SpriteLoader-MacOS10.15.app SpriteLoader-MacOS11.0.app

SpriteLoader-Pre10.15.app: dep SpriteLoader.jar
	mkdir -p SpriteLoader-Pre10.15.app/Contents/MacOS
	mkdir -p SpriteLoader-Pre10.15.app/Contents/Resources
	cp -f dep/PkgInfo-SL SpriteLoader-Pre10.15.app/Contents/PkgInfo
	cp -f dep/Info-SL.plist SpriteLoader-Pre10.15.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-Pre10.15 SpriteLoader-Pre10.15.app/Contents/MacOS/SpriteLoader
	cp -f dep/SpriteLoader.icns SpriteLoader-Pre10.15.app/Contents/Resources

SpriteLoader-MacOS10.15.app: dep SpriteLoader.jar
	mkdir -p SpriteLoader-MacOS10.15.app/Contents/MacOS
	mkdir -p SpriteLoader-MacOS10.15.app/Contents/Resources
	cp -f dep/PkgInfo-SL SpriteLoader-MacOS10.15.app/Contents/PkgInfo
	cp -f dep/Info-SL.plist SpriteLoader-MacOS10.15.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-MacOS10.15 SpriteLoader-MacOS10.15.app/Contents/MacOS/SpriteLoader
	cp -f dep/SpriteLoader.icns SpriteLoader-MacOS10.15.app/Contents/Resources

SpriteLoader-MacOS11.0.app: dep SpriteLoader.jar
	mkdir -p SpriteLoader-MacOS11.0.app/Contents/MacOS
	mkdir -p SpriteLoader-MacOS11.0.app/Contents/Resources
	cp -f dep/PkgInfo-SL SpriteLoader-MacOS11.0.app/Contents/PkgInfo
	cp -f dep/Info-SL.plist SpriteLoader-MacOS11.0.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-MacOS11.0 SpriteLoader-MacOS11.0.app/Contents/MacOS/SpriteLoader
	cp -f dep/SpriteLoader.icns SpriteLoader-MacOS11.0.app/Contents/Resources

SpriteLoader.exe: dep SpriteLoader.jar
	cp -f dep/SpriteLoader.exe SpriteLoader.exe

install: imagetool.jar
	sudo cp -f imagetool.jar /usr/local/lib/
	sudo chmod -f 644 /usr/local/lib/imagetool.jar
	sudo cp -i dep/imagetool /usr/local/bin/
	sudo chmod -f 755 /usr/local/bin/imagetool
	sudo cp -i dep/listimages /usr/local/bin/
	sudo chmod -f 755 /usr/local/bin/listimages
	sudo cp -i dep/viewimages /usr/local/bin/
	sudo chmod -f 755 /usr/local/bin/viewimages
	sudo cp -i dep/transformimages /usr/local/bin/
	sudo chmod -f 755 /usr/local/bin/transformimages
	sudo cp -i dep/convertanimation /usr/local/bin/
	sudo chmod -f 755 /usr/local/bin/convertanimation

uninstall:
	if [ -f /usr/local/bin/imagetool ]; then sudo rm -i /usr/local/bin/imagetool; fi
	if [ -f /usr/local/bin/listimages ]; then sudo rm -i /usr/local/bin/listimages; fi
	if [ -f /usr/local/bin/viewimages ]; then sudo rm -i /usr/local/bin/viewimages; fi
	if [ -f /usr/local/bin/transformimages ]; then sudo rm -i /usr/local/bin/transformimages; fi
	if [ -f /usr/local/bin/convertanimation ]; then sudo rm -i /usr/local/bin/convertanimation; fi
	if [ -f /usr/local/bin/imagetool.jar ]; then sudo rm -i /usr/local/bin/imagetool.jar; fi
	if [ -f /usr/local/lib/imagetool.jar ]; then sudo rm -i /usr/local/lib/imagetool.jar; fi

.PHONY: all eclipseall clean eclipseclean SpriteLoader.app install uninstall
