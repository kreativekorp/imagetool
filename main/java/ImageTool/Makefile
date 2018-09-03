SRCFILES := $(shell find src/com/kreative/imagetool -type f \! -path \*/.\* -name \*.java)
RESFILES := $(shell find src/com/kreative/imagetool -type f \! -path \*/.\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src/com/kreative/imagetool -type d \! -path \*/.\* | sed s/^src\\/// | tr / .)

all: clean bin doc imagetool.jar imagetool-src.tgz SpriteLoader.jar

eclipseall: eclipseclean imagetool.jar imagetool-src.tgz SpriteLoader.jar

clean:
	rm -rf bin
	rm -rf doc
	rm -rf imagetool*.jar
	rm -rf imagetool*.tgz
	rm -rf SpriteLoader.jar

eclipseclean:
	rm -rf imagetool*.jar
	rm -rf imagetool*.tgz
	rm -rf SpriteLoader.jar

bin: src
	mkdir -p bin
	javac -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc -sourcepath src $(PACKAGES) -d doc

imagetool.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF imagetool.jar -C bin com/kreative

imagetool-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf imagetool-src.tgz src/com/kreative

SpriteLoader.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST-SL.MF SpriteLoader.jar -C bin com/kreative

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

.PHONY: all eclipseall clean eclipseclean install uninstall
