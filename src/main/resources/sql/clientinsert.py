import os

x = ["users"]
for i in x:
    os.chdir("datacreation/{}".format(i))
    os.popen("python makeinsert.py")
    os.chdir("../..")

y = ["roleinsert.sql", "uinserts.sql"]

with file("../import.sql", 'w') as fh:
    for i in y:
        with open(i, 'r') as f:
            for j in f:
                fh.write(j)
        fh.write("\n");
