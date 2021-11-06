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
outputfilename = "../../tvinserts.sql"


def makespecs(x):
    return ("type:{},size:{},resolution:{},frequency:{},brand:{},smart:{}").format(*x)

tags = "Computing"
category = "tv"
def makeinsert(ls):
    rating = random.randint(0,5)
    return 'INSERT INTO {} ({}, {}, {}, {}, {}, {}, {}, rated) VALUES ("{}", "{}", {}, "{}", "{}", "{}", {}, 1);'.format(tablename, namename, specsname, ratingname, descriptionname, categoryname, tagsname, withdrawnname, ls[2], makespecs(ls[5:11]), rating, ls[1], category, tags, "FALSE")

with file(outputfilename, 'w') as fh:
    with open("tvs.csv", 'r') as fhandle:
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
