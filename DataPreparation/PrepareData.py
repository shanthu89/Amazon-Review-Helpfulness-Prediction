#Opening the given file Foods.txt dataset
file=open('E:\\Shanth\\Big data\\Project\\Py script\\foods.txt','r')
#Creating new empty list to store the three columns needed for this problem.
review_helpfulness=[]
review_score=[]
review_text=[]
helpfulnessThreshold=2
#Read through all lines till end of the file.
for line in file:
    line=line.strip()
#If line startswith "review/helpfulness" or "review/score" or "review/text" we go through that line and parse the string & split it with separators like "/" and ":"
#If review/helpfulness : calculate a/b ratio. a - Number of helpful votes. b - total votes.
#If review/score : extract data after ":" symbol.
#If review/text : calculate length of text.
    if line.startswith("review/helpfulness") or line.startswith("review/score") or line.startswith("review/text"):              
        if line.startswith("review/helpfulness"):
            rev_help=line.split(sep='/')
            b=int(rev_help[2])
            if(b >= helpfulnessThreshold):
               Flag=1
               a=rev_help[1].split(sep=':')
               a=a[1].strip()
               c= float(int(a)/b)
               c=round(c,4)
               review_helpfulness.append(c)             
            else:
               Flag=0         
        elif (line.startswith("review/score")) and (Flag==1):
            rev_score=line.split(sep=':')
            score=rev_score[1].strip()
            sc=float(score)
            review_score.append(sc)       
        elif (line.startswith("review/text"))and (Flag==1):
            rev_txt=line.split(sep=':')
            rev_txt=rev_txt[1].strip()
            text_length=len(rev_txt)
            review_text.append(text_length)
# Open new file and write the data extracted only from those three columns.
f=open("Sample_food_processed.txt",'w')
for i in range(len(review_text)):
   print("Processing line" , i) # To track which line is currently processed.
   f.write("%s," % review_helpfulness[i])
   f.write("%s," % review_score[i])
   f.write("%s\n" % review_text[i])
f.close()



