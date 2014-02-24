#!/usr/bin/perl

#This is the Helicopter makeJars.pl
mkdir("provingJars");
chdir("tmp-unjar");
 
for my $i (0..99){
	system "cp ../JarWithAllClasses.jar .";
	@output = `unzip JarWithAllClasses.jar`;
	system "rm -f JarWithAllClasses.jar";
	
	foreach my $line (@output) {
		if($line =~ /creating: (.*)/) {
			$line = $1;
		}
		if($line =~ /HPMDP(\d+)/ && $1 != $i){
			unlink("HPMDP$1/HPMDP$1.class");		
			rmdir "HPMDP$1";
		}
	}
 
	system "jar -cmf META-INF/MANIFEST.MF HPMDP$i.jar *";
	system "cp HPMDP$i.jar ../provingJars/";
	system "rm -rf *";
}