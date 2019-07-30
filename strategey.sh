cp -r Strategy/Parallel-Node Parallel-Node 
cp -r Strategy/Parallel-Partition Parallel-Partition
cp -r Strategy/Serial Serial
cp -r Strategy/Sampling Sampling

echo "\nExecution of Program in Parallel-Node Based Allocation\n"
cd Parallel-Node
rm -r *.class 2> /dev/null
rm -r *.txt 2> /dev/null
javac testDyNeMoC.java
/usr/bin/time -v java testDyNeMoC

echo "\nExecution of Program in Parallel-Partition Based Allocation\n"
cd ../Parallel-Partition
rm -r *.class 2> /dev/null
rm -r *.txt 2> /dev/null
javac testDyNeMoC.java
/usr/bin/time -v java testDyNeMoC

echo "\nExecution of Program in Serial\n"
cd ../Serial
rm -r *.class 2> /dev/null
rm -r *.txt 2> /dev/null
javac testDyNeMoC.java
/usr/bin/time -v java testDyNeMoC

echo "\nExecution of Program in Sampling\n"
cd ../Sampling
rm -r *.class 2> /dev/null
rm -r *.txt 2> /dev/null
javac testDyNeMoC.java
/usr/bin/time -v java testDyNeMoC
