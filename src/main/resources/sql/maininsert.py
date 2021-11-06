import os

x = ["katastimata", "laptop", "phone", "tablet", "tv", "triplerelation", "users"]
for i in x:
    os.chdir("datacreation/{}".format(i))
    os.popen("python makeinsert.py")
    os.chdir("../..")

y = ["inserts.sql", "phoneinserts.sql", "tabletinserts.sql", "tvinserts.sql", "kinserts.sql", "roleinsert.sql", "uinserts.sql", "triple.sql"]

with file("../import.sql", 'w') as fh:
    for i in y:
        with open(i, 'r') as f:
            for j in f:
                fh.write(j)
        fh.write("\n");
