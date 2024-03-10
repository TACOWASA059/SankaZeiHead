import numpy as np
import os
import cv2
import re 
#スキンの拡大率指定[32x64]
size_multi=2

class OutputAssetsExecutor:
    def __init__(self, modid:str, SkinDirName:str):
        '''
        modid : mod id
        SkinDirName : directory of Player Skin
        '''
        self.modid = modid
        self.SkinDirName = SkinDirName
        os.makedirs("assets/"+modid,exist_ok=True)
        self.extract_player_head_from_directory()
        self.output_Json()

    def add_underscore(self,s:str)->str:
        '''
        文字列が数字から始まるときは前に_をつける
        '''
        s=s.lower()
        if re.match(r'^\d', s):
            return '_' + s
        else:
            return s
    def extract_player_head_from_directory(self):
        '''
        プレイヤースキンリストから頭だけを取り出して保存する
        '''
        self.png_filenames = []
        self.png_original_filenames=[]
        for file in sorted(os.listdir(self.SkinDirName)):
            if file.endswith(".png"):
                self.png_filenames.append(self.add_underscore(file.replace(".png","")))
                self.png_original_filenames.append(file.replace(".png",""))
        for input_file in self.png_original_filenames:
            self.extract_player_head(self.SkinDirName, input_file)
    def calc_outer_coord(self, x:int, flag:bool=True)->int:
        '''
        上位レイヤーの位置計算
        '''
        layer1_coord=float(x)/size_multi
        center=float((int(layer1_coord)/8)*8)+3.5
        layer2_coord=center+(layer1_coord-center)*10.0/9.0
        layer2_coord=int(np.clip(layer2_coord, center-3.5, center+3.5))
        return layer2_coord
    def extract_player_head(self,directory_path:str,fname:str):
        '''
        各スキンから頭だけを取り出す
        '''
        img=cv2.imread(directory_path+"/"+fname+".png",-1)
        img2=np.zeros(shape=(16*size_multi,32*size_multi,4))
        for i in range(16):
            for j in range(32):
                for k in range(size_multi):
                    for l in range(size_multi):
                        img2[size_multi*i+k][size_multi*j+l]=img[i][j]
        for i in range(16):
            for j in range(32,64):
                for k in range(size_multi):
                    for l in range(size_multi):
                        if img[self.calc_outer_coord(size_multi*i+k)][self.calc_outer_coord(size_multi*j+l,False)][3]!=0:
                            img2[size_multi*i+k][size_multi*(j-32)+l]=img[self.calc_outer_coord(size_multi*i+k)][self.calc_outer_coord(size_multi*j+l,False)]
        self.texture_path= "assets/"+self.modid+"/textures/block/"
        os.makedirs(self.texture_path ,exist_ok=True)
        cv2.imwrite(self.texture_path + self.add_underscore(fname.lower())+".png",img2)
    def output_Json(self):
        '''
        blockstates/の作成
        models/block/の作成
        models/item/の作成
        lang/en_us.jsonの作成
        lang/ja_jp.jsonの作成
        '''
        for input_file in self.png_filenames:
            self.output_blockstates_Content(input_file)
            self.output_block_Content(input_file)
            self.output_item_Content(input_file)
        self.output_lang_Content()
    def output_blockstates_Content(self,input_file_name:str):
        path:str="assets/"+self.modid+"/blockstates/"
        os.makedirs(path,exist_ok=True)
        with open(path+input_file_name+".json","w") as f:
            f.write('{\n"variants":{\n"facing=north":{\n"model":"'+self.modid+':block/'+input_file_name+'"\n},\n"facing=west":{\n"model":"'+self.modid+':block/'+input_file_name+'",\n"y":270\n},\n"facing=east":{\n"model":"'+self.modid+':block/'+input_file_name+'",\n"y":90\n},\n"facing=south":{\n"model":"'+self.modid+':block/'+input_file_name+'",\n"y":180\n},\n"facing=up":{\n"model":"'+self.modid+':block/'+input_file_name+'",\n"x":270\n},\n"facing=down":{\n"model":"'+self.modid+':block/'+input_file_name+'",\n"x":90\n}\n}\n}')
    def output_block_Content(self,input_file_name:str):
        path:str="assets/"+self.modid+"/models/"+"block/"
        os.makedirs(path,exist_ok=True)
        with open(path+input_file_name+".json","w") as f:
            f.write('{\n"texture_size": [64, 32],\n"textures": {\n"0": "'+self.modid+':block/'+input_file_name+'",\n"particle": "'+self.modid+':block/'+input_file_name+'"\n},\n"elements": [\n{\n"from": [0, 0, 0],\n"to": [16, 16, 16],\n"faces": {\n"north": {"uv": [4, 8, 8, 16], "texture": "#0"},\n"east": {"uv": [0, 8, 4, 16], "texture": "#0"},\n"south": {"uv": [12, 8, 16, 16], "texture": "#0"},\n"west": {"uv": [8, 8, 12, 16], "texture": "#0"},\n"up": {"uv": [4, 0, 8, 8], "rotation": 180, "texture": "#0"},\n"down": {"uv": [12, 0, 8, 8], "texture": "#0"}\n}\n}\n],\n"display": {\n"thirdperson_righthand": {\n"rotation": [92, 0, 0],\n"scale": [0.35, 0.35, 0.35]\n},\n"thirdperson_lefthand": {\n"rotation": [87, 0, 0],\n"scale": [0.35, 0.35, 0.35]\n},\n"firstperson_righthand": {\n"scale": [0.32, 0.32, 0.32]\n},\n"firstperson_lefthand": {\n"scale": [0.32, 0.32, 0.32]\n},\n"ground": {\n"scale": [0.5, 0.5, 0.5]\n},\n"gui": {\n"rotation": [22, -140, -2],\n"scale": [0.65, 0.65, 0.65]\n},\n"head": {\n"scale": [0.85, 0.85, 0.85]\n}\n}\n}')
    def output_item_Content(self,input_file_name:str):
        path:str="assets/"+self.modid+"/models/"+"item/"
        os.makedirs(path,exist_ok=True)
        with open(path+input_file_name+".json","w") as f:
            f.write('{\n"parent": "'+self.modid+':block/'+input_file_name+'"\n}')
    def output_lang_Content(self):
        path="assets/"+self.modid+"/lang/"
        os.makedirs(path,exist_ok=True)
        with open(path+"en_us.json","w") as f:
            f.write("{\n")
            f.write(f'\t"itemGroup.'+self.modid+'":"sankazei head MOD",\n')
            for i,input_file in enumerate(self.png_filenames):
                if i==len(self.png_filenames)-1:
                    f.write(f'\t"block.'+self.modid+f'.{input_file}": "{input_file}"\n')
                else:
                    f.write(f'\t"block.'+self.modid+f'.{input_file}": "{input_file}",\n')
            f.write("\n}")
        with open(path+"ja_jp.json","w") as f:
            f.write("{\n")
            f.write(f'\t"itemGroup.'+self.modid+'":"参加勢ヘッドMOD",\n')
            for i,input_file in enumerate(self.png_filenames):
                if i==len(self.png_filenames)-1:
                    f.write(f'\t"block.'+self.modid+f'.{input_file}": "{input_file}"\n')
                else:
                    f.write(f'\t"block.'+self.modid+f'.{input_file}": "{input_file}",\n')
            f.write("\n}")
    def gen_enum(self,filename:str):
        '''
        enumの出力
        '''
        with open(filename+".java","w") as f:
            f.write("public enum " + filename + " {\n")
            for input in self.png_filenames:
                f.write("    " + input + ",\n")
            f.write("}\n")
    def output_Tags(self):
        '''
        worldedit用のTagを出力
        '''
        path="data/"+self.modid+"/tags/blocks/"
        os.makedirs(path,exist_ok=True)
        with open(path+"head_blocks.json","w") as f:
            f.write("{\n")
            f.write('\t"replace": false,\n')
            f.write('\t"values": [\n')
            for i,input_file in enumerate(self.png_filenames):
                input_file=input_file.replace(".png","")
                if(i<len(self.png_filenames)-1):
                    f.write(f'\t\t"'+self.modid+f':{input_file}",\n')    
                else:
                    f.write(f'\t\t"'+self.modid+f':{input_file}"\n')    
            f.write("\t]")
            f.write("\n}")
if __name__=="__main__":
    e=OutputAssetsExecutor(modid="sankazeihead",SkinDirName="SkinData")
    e.gen_enum("SankaZeiList")
    e.output_Tags()