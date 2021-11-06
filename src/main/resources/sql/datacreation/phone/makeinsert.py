# -*- coding: utf-8 -*-
#cpu,ram,storage,graphics
import random
#sqlstuff
tablename = "product"

brandname = "brand"
descriptionname = "description"
namename = "model"
categoryname = "category"
ratingname = "rating"
withdrawnname = "withdrawn"
specsname = "specs"
tagsname = "tags"
outputfilename = "../../phoneinserts.sql"

tags = "Computing"
category = "phone"
def makeinsert(ls):
    rating = random.randint(1,5)
    return 'INSERT INTO {} ({}, {}, {}, {}, {}, {}, {}, rated) VALUES ("{}", "{}", {}, "{}", "{}", "{}", {}, 1);'.format(tablename, namename, specsname, ratingname, descriptionname, categoryname, tagsname, withdrawnname, ls[0], ls[1].replace("screen:", "screen size/technology:").replace("Μνήμη (RAM/ROM):", "ram/rom:"), rating, ls[2], category, tags, "FALSE")

with file(outputfilename, 'w') as fh:
    with open("Workbook1.csv", 'r') as fhandle:
        fhandle.readline()
        fh.write("USE cheapset;\n")
        for i in fhandle:
            line = map(lambda a : a.replace('"','\\"').strip(), i.split(';'))
            if len(line[0]) < 2:
                continue
            try:
                temp = makeinsert(line) + '\n'
                fh.write(temp)
            except:
                pass
