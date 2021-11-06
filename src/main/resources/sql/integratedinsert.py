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
    fh.write('INSERT INTO product (model, specs, rating, description, category, tags, withdrawn, rated) VALUES ("name1", "", 0.0, "description1", "category", "tag1", FALSE, 1);\n')
    fh.write('INSERT INTO product (model, specs, rating, description, category, tags, withdrawn, rated) VALUES ("name2", "", 0.0, "description2", "category", "tag2", FALSE, 1);\n')
    fh.write('INSERT INTO product (model, specs, rating, description, category, tags, withdrawn, rated) VALUES ("name3", "", 0.0, "description3", "category", "tag3", FALSE, 1);\n')
    fh.write('INSERT INTO product (model, specs, rating, description, category, tags, withdrawn, rated) VALUES ("name5", "", 0.0, "description5", "category", "tag1", FALSE, 1);\n')
