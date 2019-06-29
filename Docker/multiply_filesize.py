import os.path
import os
import io
import sys

if len(sys.argv) < 2:
        print("please give one start-directory as argument\nExample: './Languages/'")
        exit()

startpath = sys.argv[1]
multiplier = 2

print("Increasing Textfiles\nStarting at: " + startpath + ".\nMultiplying by: " + str(multiplier))

for root, dirs, files in os.walk(startpath):
    path = root.split(os.sep)
    print((len(path) - 1) * '---', os.path.basename(root))
    for file in files:
        if ".txt" in file:
                txtfile = os.path.join(root, file)
                print(txtfile)
                contents = ""
                with io.open(txtfile, encoding='utf-8') as f:
                        contents = f.read()
                with io.open(txtfile, encoding="utf-8", mode="w") as f:
                        for i in range(multiplier):
                                f.write(contents)