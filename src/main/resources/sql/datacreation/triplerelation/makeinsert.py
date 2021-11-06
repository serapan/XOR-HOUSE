# -*- coding: utf-8 -*-
import random

dates = [("2018-12-1", "2018-12-6"),("2018-12-7", "2018-12-12"),("2018-12-13", "2018-12-17"),("2018-12-18", "2018-12-24"),("2018-12-25", "2018-12-29")]

with open("uname.txt", 'r') as fhandle:
    usernames = filter(lambda a: len(a) > 0, fhandle.read().split('\n'))

with open("KATASTIMATA.csv", 'r') as fhandle:
    data = fhandle.read().split('\n')
    katastimata = map(lambda a: a.split(';')[0].strip(), filter(lambda a: len(a) > 0, data))
    x = map(lambda a: a.split(';')[4].strip(), filter(lambda a: len(a) > 0, data))
    y = map(lambda a: a.split(';')[5].strip(), filter(lambda a: len(a) > 0, data))

laptopids = [str(i).zfill(6) for i in range(1,273)]

def randomprice():
    return str(round(1000 + (random.random() * 1.2 - 0.4) * 1000, 2))

def getkatastima(used):
    while True:
        i = random.randint(0, len(katastimata) - 1)
        if (katastimata[i], x[i], y[i]) in used:
            continue
        break
    return i

with file("../../triple.sql", 'w') as fh:
    fh.write("USE cheapset;\n\n")
    for i in laptopids:
        used = set()
        for j in range(5):
            k = getkatastima(used)
            used.add((katastimata[k], x[k], y[k]))
            user = random.randint(0, len(usernames) - 1)
            for mm in dates:
                fh.write('INSERT INTO info (user_id, sku, store_id, price, date, date_from, date_to) SELECT user_id, sku, store_id, {}, "{}", "{}", "{}" FROM user, product, store WHERE username = "{}" AND sku = "{}" AND store.name = "{}" AND lng = {} AND lat = {};\n'.format(randomprice(), "2020-12-19", mm[0], mm[1], usernames[user], i, katastimata[k].strip(), y[k].replace(",", ".").strip(), x[k].replace(",", ".").strip()))
