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
outputfilename = "../../tabletinserts.sql"


def makespecs(screen, con, cpu, ram, brand):
    return ("screen size/technology:{},connectivity:{},cpu:{},ram/rom:{},brand:{}").format(screen, con, cpu, ram, brand)

tags = "Computing"
category = "tablet"
def makeinsert(ls):
    rating = random.randint(0,5)
    return 'INSERT INTO {} ({}, {}, {}, {}, {}, {}, {}, rated) VALUES ("{}", "{}", {}, "{}", "{}", "{}", {}, 1);'.format(tablename, namename, specsname, ratingname, descriptionname, categoryname, tagsname, withdrawnname, ls[2], makespecs(*ls[5:10]), rating, ls[1], category, tags, "FALSE")

with file(outputfilename, 'w') as fh:
    with open("tablets.csv", 'r') as fhandle:
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
