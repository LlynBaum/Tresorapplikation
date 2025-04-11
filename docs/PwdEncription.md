# PWD Encription

## Hash

Ein Algoritmos der nicht umkehr bar ist. Die Idee, man Hasht ein Password, damit man auch wen man access zur DB hat keine ahnung hat was das Password ist. Man hat nur unleserlicher Text. Aber wen man ein Password Hashed und dan mit dem Hash in der DB vergleicht kann man immer noch das Password vergleichen.

## Paper

Ein String der zum Password hinzugefügt wird das ein Secret auf dem Server ist in der Config. Das wird das Password mit dem Paper zusammen gehashed. So ist jeder Hash noch ein wenig Uniquer für dein Server.

## Salt

Das Selbe wie Paper aber anstelle das es auf dem Server ist, sit es in der DB oder so und für jeden user anderst. So ist auch wen zwei personen das selbe Password haben, haben sie ein anderen Hash.