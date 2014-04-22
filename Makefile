##
## This is just because I am to lazy to learn new commands.
##
## Moreover, the 'prepare' section removes trailing spaces
## as replaces tabs by 4 spaces. This is needed (sometimes)
## to meet the checkstyle criterias.
##
## @author: Michael Bredel <michael.bredel@cern.ch>
##

all: prepare clean install

clean:
	@mvn clean

install: 
	@mvn install

prepare:
	@find ./ -type f -name *.java -exec sed -i -E 's/[[:space:]]*$$//' \{} \;
	@find ./ -type f -name *.java -exec sed -i 's/\t/    /g' {} \;
	@find ./ -type f -name pom.xml -exec sed -i -E 's/[[:space:]]*$$//' \{} \;
	@find ./ -type f -name pom.xml -exec sed -i 's/\t/    /g' {} \;

