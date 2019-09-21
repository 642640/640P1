JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES := \
		$(shell echo *.java)

default: classes

classes: $(CLASSES:.java=.class)

clean:
	rm *.class
