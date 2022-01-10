package com.example.clientapp;

import static com.example.clientapp.findroom.mSocket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class gameboard extends AppCompatActivity {
    String opponentname="",id="",roomnumber="";
    Gson gson = new Gson();
    LinearLayout.LayoutParams cellparams,wallparams,pointparams;
    ConstraintLayout constly;
    LinearLayout leftwallandtimer;
    LinearLayout controlpanel;
    int[][] walls=new int[15][15];//0 initial 1 horizontal 2 vertical
    boolean[][][] possiblept = new boolean[15][15][2];// sector 0 for horizontal, 1 for vertical
    boolean[][] possiblecell = new boolean[15][15];
    ArrayList<Point> pcells = new ArrayList<Point>();
    boolean[][][] con = new boolean[15][15][4];
    boolean[][] vis = new boolean[15][15];
    boolean isgameover= false;
    int[] dx = {1,-1,0,0};
    int[] dy = {0,0,-1,1}; //eswn
    int[] orth={2,2,0,0};
    Button[][] btns = new Button[15][15];
    Button[][] points = new Button[15][15];
    Button choose_wall,choose_move,confirm_wall,confirm_move,wall_back,move_back,gotomenu;
    ToggleButton togglebtn;
    TextView tv_myname,tv_opname,tv_mystatus,tv_opstatus,tv_left_walls,lefttime;
    ProgressBar pgbar;
    int px,py,ox,oy;
    String pickedcellcol="#FF0000",possiblecellcol="#888888",cellcol="#8B4513";
    static boolean ismyturn=true;
    int scrx,scry,cellx,celly;
    int recent_cell_x=5,recent_cell_y=5,recent_wall_x=5,recent_wall_y=5;
    CountDownTimer mytimer;
    char recent_ori='h';
    int gap=35;
    int leftwall=10;
    int maxtime = 30;
    String opponent_wall_color = "#FFFFFF";
    String my_wall_color = "#000000";
    ImageView playerimage,opimage;
    boolean isMoveChoosed=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        leftwall=10;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        opponentname=bundle.getString("opponentname");
        id=bundle.getString("id");
        roomnumber=bundle.getString("roomid");
        setContentView(R.layout.activity_gameboard);
        mSocket.emit("newMessage",gson.toJson(new MessageData(
                id,
                roomnumber,
                opponentname,
                "",
                ""
        )));
        px=5;py=1;ox=5;oy=9;
        createBoard();
        choose_move=findViewById(R.id.choose_move);
        choose_wall=findViewById(R.id.choose_wall);
        confirm_move=findViewById(R.id.confirm_move);
        confirm_wall=findViewById(R.id.confirm_wall);
        togglebtn=findViewById(R.id.togglebtn);
        wall_back=findViewById(R.id.wall_back);
        move_back=findViewById(R.id.move_back);
        gotomenu = findViewById(R.id.gotomenu);

        tv_myname = findViewById(R.id.tv_myname);
        tv_opname = findViewById(R.id.tv_opname);
        tv_mystatus = findViewById(R.id.tv_mystatus);
        tv_opstatus = findViewById(R.id.tv_opstatus);
        tv_left_walls = findViewById(R.id.tv_left_walls);

        leftwallandtimer = findViewById(R.id.leftwallandtimer);
        lefttime= findViewById(R.id.lefttime);

        pgbar = findViewById(R.id.progressBar);
        pgbar.setMax(maxtime*1000);

        tv_myname.setText("나 : "+id);
        tv_opname.setText("상대 : "+opponentname);

        hideallcontrolpanel();

        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                for(int k=0;k<4;k++)con[i][j][k]=true;
            }
        }
        for(int i=1;i<=9;i++)vis[0][i]=vis[i][0]=vis[10][i]=vis[i][10]=true;
        for(int i=1;i<=9;i++){
            con[i][1][2]=false;
            con[i][9][3]=false;
            con[1][i][1]=false;
            con[9][i][0]=false;
        }

        choose_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMoveChoosed = true;
                choose_move.setVisibility(View.GONE);
                choose_wall.setVisibility(View.GONE);
                move_back.setVisibility(View.VISIBLE);
                confirm_move.setVisibility(View.VISIBLE);
                for(Point pt : pcells){
                    btns[pt.x][10-pt.y].setBackgroundColor(Color.parseColor(possiblecellcol));
                }
            }
        });

        choose_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_move.setVisibility(View.GONE);
                choose_wall.setVisibility(View.GONE);
                wall_back.setVisibility(View.VISIBLE);
                togglebtn.setVisibility(View.VISIBLE);
                confirm_wall.setVisibility(View.VISIBLE);
                for(Point pt : pcells){
                    btns[pt.x][10-pt.y].setBackgroundColor(Color.parseColor(cellcol));
                }
                showpointchoices();
            }
        });

        confirm_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ismyturn)return;
                if(recent_cell_x==0||recent_cell_y==0)return;
                choose_move.setVisibility(View.VISIBLE);
                if(leftwall>0)choose_wall.setVisibility(View.VISIBLE);
                move_back.setVisibility(View.GONE);
                confirm_move.setVisibility(View.GONE);
                mSocket.emit("turnend",gson.toJson(new MessageData(
                        id,
                        roomnumber,
                        opponentname,
                        "",
                        "m"+(10-recent_cell_x)+(10-recent_cell_y)
                )));
                moveme(recent_cell_x,recent_cell_y);
                for(Point pt : pcells){
                    btns[pt.x][10-pt.y].setBackgroundColor(Color.parseColor(cellcol));
                }
                mytimer.cancel();
            }
        });
        recent_ori='h';
        togglebtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recent_ori='v';
                } else {
                    recent_ori='h';
                }
                updatewallcandidate(recent_wall_x,recent_wall_y,recent_wall_x,recent_wall_y);
            }
        });

        confirm_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ismyturn)return;
                if(recent_wall_x==0||recent_wall_y==0)return;
                choose_move.setVisibility(View.VISIBLE);
                wall_back.setVisibility(View.GONE);
                togglebtn.setVisibility(View.GONE);
                confirm_wall.setVisibility(View.GONE);
                buildwall(recent_wall_x,recent_wall_y,recent_ori,Color.parseColor(my_wall_color));
                leftwall--;
                tv_left_walls.setText("left wall : "+leftwall);
                if(leftwall>0)choose_wall.setVisibility(View.VISIBLE);
                mSocket.emit("turnend",gson.toJson(new MessageData(
                        id,
                        roomnumber,
                        opponentname,
                        "",
                        "w"+(9-recent_wall_x)+(9-recent_wall_y)+recent_ori
                )));
                backtooriginwall(recent_wall_x,recent_wall_y);
                hidepointchoices();
                for(Point pt : pcells){
                    btns[pt.x][10-pt.y].setBackgroundColor(Color.parseColor(cellcol));
                }
                mytimer.cancel();
            }
        });

        move_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_move.setVisibility(View.VISIBLE);
                if(leftwall>0)choose_wall.setVisibility(View.VISIBLE);
                move_back.setVisibility(View.GONE);
                confirm_move.setVisibility(View.GONE);
                for(Point pt : pcells){
                    btns[pt.x][10-pt.y].setBackgroundColor(Color.parseColor(cellcol));
                }
                isMoveChoosed=false;
            }
        });

        wall_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_move.setVisibility(View.VISIBLE);
                if(leftwall>0)choose_wall.setVisibility(View.VISIBLE);
                wall_back.setVisibility(View.GONE);
                togglebtn.setVisibility(View.GONE);
                confirm_wall.setVisibility(View.GONE);
                hidepointchoices();
            }
        });

        gotomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gameboard.this, MenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                intent.putExtras(bundle);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                if(mSocket!=null)mSocket.disconnect();
            }
        });

        mSocket.emit("ready",gson.toJson(new MessageData(
            id,
            roomnumber,
            opponentname,
            "",
                ""
        )));
        mSocket.emit("getmystatus",gson.toJson(new MessageData(
                id,roomnumber,opponentname,"",""
        )));
        mSocket.emit("getmystatus",gson.toJson(new MessageData(
                opponentname,roomnumber,id,"",""
        )));
        mSocket.on("newturn",newturn);
        mSocket.on("gameover",gameover);
        mSocket.on("yourstatus",receivestatus);

    }
    void createBoard(){
        Point pt = new Point();
        this.getWindowManager().getDefaultDisplay().getRealSize(pt);
        scrx=pt.x;scry=pt.y;
        //scry=scry*8/10;
        constly = findViewById(R.id.board);
        controlpanel = findViewById(R.id.controlpanel);
        int sqsz=Integer.min(scrx,scry*8/10);
        cellx=celly=(sqsz-gap*10)/9;
        cellparams = new LinearLayout.LayoutParams(cellx,celly);
        for(int i=1;i<=9;i++) {
            for (int j = 1; j <= 9; j++) {
                btns[i][j] = new Button(this);
                btns[i][j].setLayoutParams(cellparams);
                constly.addView(btns[i][j]);
                btns[i][j].setX((float)((i-1)*(cellx+gap)+gap));
                btns[i][j].setY((float)((j-1)*(celly+gap)+gap));
                btns[i][j].setBackgroundColor(Color.parseColor("#8B4513"));
                int x=i,y=j;
                btns[i][j].setOnClickListener(new View.OnClickListener() {
                    int fx=x,fy=10-y;
                    @Override
                    public void onClick(View view) {
                        handlecelltouch(fx,fy);
                    }
                });
            }
        }
        playerimage = new ImageView(this);
        playerimage.setImageResource(R.mipmap.black_pawn_foreground);
        playerimage.setLayoutParams(cellparams);
        constly.addView(playerimage);
        opimage = new ImageView(this);
        opimage.setImageResource(R.mipmap.white_pawn_foreground);
        opimage.setLayoutParams(cellparams);
        constly.addView(opimage);
        moveme(px,py);
        moveop(ox,oy);

        for(int i=1;i<=8;i++){
            for(int j=1;j<=8;j++){
                points[i][j]=new Button(this);
                pointparams = new LinearLayout.LayoutParams(gap,gap);
                points[i][j].setLayoutParams(pointparams);
                constly.addView(points[i][j]);
                points[i][j].setX((float)((i)*(cellx+gap)));
                points[i][j].setY((float)((j)*(celly+gap)));
                possiblept[i][j][0]=possiblept[i][j][1]=true;
                points[i][j].setBackgroundColor(Color.parseColor("#FF0000"));
                int x=i,y=j;
                points[i][j].setOnClickListener(new View.OnClickListener() {
                    int fx=x,fy=9-y;
                    @Override
                    public void onClick(View view) {
                        handlepointtouch(fx,fy);
                    }
                });
                points[i][j].setVisibility(View.GONE);
            }
        }

    }

    void moveme(int x,int y){
        btns[px][10-py].setVisibility(View.VISIBLE);
        px=x;py=y;
        btns[px][10-py].setVisibility(View.GONE);
        y=10-y;
        playerimage.setX((float)((x-1)*(cellx+gap)+gap));
        playerimage.setY((float)((y-1)*(celly+gap)+gap));
    }

    void moveop(int x,int y){
        btns[ox][10-oy].setVisibility(View.VISIBLE);
        ox=x;oy=y;
        btns[ox][10-oy].setVisibility(View.GONE);
        y=10-y;
        opimage.setX((float)((x-1)*(cellx+gap)+gap));
        opimage.setY((float)((y-1)*(celly+gap)+gap));
    }

    void buildwall(int x, int y, char ori,int color){
        ImageView wall = new ImageView(this);
        wall.setBackgroundColor(color);
        if(ori=='h')wallparams = new LinearLayout.LayoutParams(cellx*2+gap,gap);
        else wallparams = new LinearLayout.LayoutParams(gap,celly*2+gap);
        wall.setLayoutParams(wallparams);
        constly.addView(wall);
        possiblept[x][y][0]=possiblept[x][y][1]=false;

        if(ori=='h'){
            wall.setX((float)((x)*(cellx+gap)-cellx));
            wall.setY((float)((9-y)*(celly+gap)));
            possiblept[x-1][y][0]=possiblept[x+1][y][0]=false;
            con[x][y][3]=con[x][y+1][2]=false;
            con[x+1][y][3]=con[x+1][y+1][2]=false;
            walls[x][y]=1;
        }
        else{
            wall.setX((float)((x)*(cellx+gap)));
            wall.setY((float)((9-y)*(celly+gap)-celly));
            possiblept[x][y-1][1]=possiblept[x][y+1][1]=false;
            con[x][y][0]=con[x+1][y][1]=false;
            con[x][y+1][0]=con[x+1][y+1][1]=false;
            walls[x][y]=2;
        }
        checkPointPossibilities();
    }

    void hidepointchoices(){
        for(int i=1;i<=8;i++){
            for(int j=1;j<=8;j++){
                //if(!possiblept[i][9-j])continue;
                points[i][j].setVisibility(View.GONE);
            }
        }
    }
    void showpointchoices(){
        for(int i=1;i<=8;i++){
            for(int j=1;j<=8;j++){
                if((!possiblept[i][9-j][0])&&(!possiblept[i][9-j][1]))continue;
                points[i][j].setVisibility(View.VISIBLE);
            }
        }
    }
    public Emitter.Listener newturn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isMoveChoosed=false;
//                    Toast.makeText(getApplicationContext(),"new turn!",Toast.LENGTH_SHORT).show();
                    MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
                    if(!id.equals(data.username)){
                        ismyturn=false;
                        hideallcontrolpanel();
                        return;
                    }
                    showbasiccontrolpanel();
                    ismyturn=true;
                    recent_cell_x=recent_wall_x=recent_cell_y=recent_wall_y=0;
                    updateboard(data.move);
                    updatecellpossibilities();
                    mytimer = new CountDownTimer(maxtime*1000, 100) {
                        public void onTick(long millisUntilFinished) {
                            pgbar.setProgress((int)millisUntilFinished);
                            lefttime.setText((int)millisUntilFinished/1000+"s");
                        }

                        public void onFinish() {
                            if(isgameover)return;
                            timeover();
                        }
                    }.start();
                }
            });
        }
    };

    public Emitter.Listener gameover = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
                    constly.removeAllViews();
                    leftwallandtimer.removeAllViews();
                    ImageView resultimg = new ImageView(gameboard.this);

                    LinearLayout.LayoutParams customparams = new LinearLayout.LayoutParams(scrx,scry*8/10);
                    resultimg.setLayoutParams(customparams);
                    if(id.equals(data.username)||data.detail.equals("disconnect")) {
                        resultimg.setImageResource(R.drawable.trophy_icon);
                    }
                    else{
                        resultimg.setImageResource(R.drawable.game_over);
                    }
                    constly.addView(resultimg);
                    hideallcontrolpanel();
                    gotomenu.setVisibility(View.VISIBLE);
                    isgameover=true;
//                    Toast.makeText(gameboard.this,"game over!",Toast.LENGTH_SHORT).show();
//                    mSocket.emit("left",gson.toJson(new MessageData(
//                            id,
//                            roomnumber,
//                            opponentname,
//                            "",
//                            ""
//                    )));
                    if(data.detail.equals("waive")||data.detail.equals("disconnect")){
//                        Toast.makeText(gameboard.this,"상대가 게임을 나갔습니다",Toast.LENGTH_LONG).show();
                    }
                    else if(data.detail.equals("timeover")){
//                        Toast.makeText(gameboard.this,"상대가 시간초과로 패배했습니다",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    public Emitter.Listener receivestatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
                    if(id.equals(data.username)){
                        tv_mystatus.setText(data.win+"승 "+data.lose+"패");
                    }
                    if(opponentname.equals(data.username)){
                        tv_opstatus.setText(data.win+"승 "+data.lose+"패");
                    }
                }
            });
        }
    };

    void updateboard(String move){
        char type=move.charAt(0);
        int x=Integer.parseInt(""+move.charAt(1));
        int y=Integer.parseInt(""+move.charAt(2));
        if(type=='m'){
            moveop(x,y);
        }
        else{
            char ori=move.charAt(3);
            buildwall(x,y,ori,Color.parseColor(opponent_wall_color));
        }
    }

    void handlecelltouch(int x,int y){
        if(!ismyturn)return;
        if(!isMoveChoosed)return;
        if(!possiblecell[x][y])return;
        if(recent_cell_x!=0&&recent_cell_y!=0){
            btns[recent_cell_x][10-recent_cell_y].setBackgroundColor(Color.parseColor(possiblecellcol));
        }
//        Toast.makeText(getApplicationContext(),x+" , "+y+" cell touched",Toast.LENGTH_SHORT).show();
        recent_cell_x=x;
        recent_cell_y=y;
        btns[recent_cell_x][10-recent_cell_y].setBackgroundColor(Color.parseColor(pickedcellcol));
    }
    void handlepointtouch(int x,int y){
        if(!ismyturn)return;
//        Toast.makeText(getApplicationContext(),x+" , "+y+" point touched",Toast.LENGTH_SHORT).show();
        int rx=recent_wall_x,ry=recent_wall_y;
        recent_wall_x=x;recent_wall_y=y;
        updatewallcandidate(rx,ry,x,y);
        togglebtn.setChecked(false);
        recent_ori='h';
        if(!possiblept[x][y][0]){
            togglebtn.setChecked(true);
            recent_ori='v';
        }
        if(!possiblept[x][y][0]||!possiblept[x][y][1]){
            togglebtn.setVisibility(View.GONE);
        }
        else{
            togglebtn.setVisibility(View.VISIBLE);
        }
    }

    void updatewallcandidate(int orgx,int orgy,int x,int y){
        if(x==0||y==0)return;
        backtooriginwall(orgx,orgy);
        if(recent_ori=='h')pointparams = new LinearLayout.LayoutParams(gap+gap*2,gap);
        else pointparams = new LinearLayout.LayoutParams(gap,gap+gap*2);
        points[x][9-y].setLayoutParams(pointparams);
        if(recent_ori=='h') {
            points[x][9-y].setX((float) ((x) * (cellx + gap)-gap));
            points[x][9-y].setY((float) ((9 - y) * (celly + gap)));
        }
        else{
            points[x][9-y].setX((float) ((x) * (cellx + gap)));
            points[x][9-y].setY((float) ((9 - y) * (celly + gap)-gap));
        }
    }
    void backtooriginwall(int x,int y){
        if(x==0||y==0)return;
        pointparams = new LinearLayout.LayoutParams(gap,gap);
        points[x][9-y].setLayoutParams(pointparams);
        points[x][9-y].setX((float)((x)*(cellx+gap)));
        points[x][9-y].setY((float)((9-y)*(celly+gap)));
    }
    void checkPointPossibilities(){
        for(int i=1;i<=8;i++){
            for(int j=1;j<=8;j++){
                if(walls[i][j]>0)continue;
                Boolean b1,b2,b3,b4;

                for(int ii=1;ii<=9;ii++){// px,py,hor
                    for(int jj=1;jj<=9;jj++)vis[ii][jj]=false;
                }
                b1=con[i][j][3];b2=con[i][j+1][2];
                b3=con[i+1][j][3];b4=con[i+1][j+1][2];
                con[i][j][3]=con[i][j+1][2]=false;
                con[i+1][j][3]=con[i+1][j+1][2]=false;
                vis[i][j]=true;
                dfs(px,py);
                con[i][j][3]=b1;con[i][j+1][2]=b2;
                con[i+1][j][3]=b3;con[i+1][j+1][2]=b4;
                int c=0;
                for(int k=1;k<=9;k++)if(vis[k][9])c++;
                if(c==0)possiblept[i][j][0]=false;
                //if(c==0)Toast.makeText(this,"wtf??",Toast.LENGTH_SHORT).show();
                /////////////////////////////////////
                for(int ii=1;ii<=9;ii++){//px,py, ver
                    for(int jj=1;jj<=9;jj++)vis[ii][jj]=false;
                }
                b1=con[i][j][0];b2=con[i+1][j][1];
                b3=con[i][j+1][0];b4=con[i+1][j+1][1];
                con[i][j][0]=con[i+1][j][1]=false;
                con[i][j+1][0]=con[i+1][j+1][1]=false;
                vis[i][j]=true;
                dfs(px,py);
                con[i][j][0]=b1;con[i+1][j][1]=b2;
                con[i][j+1][0]=b3;con[i+1][j+1][1]=b4;
                c=0;
                for(int k=1;k<=9;k++)if(vis[k][9])c++;
                if(c==0)possiblept[i][j][0]=false;
                //if(c==0)Toast.makeText(this,"wtf??",Toast.LENGTH_SHORT).show();
                /////////////////////////////////////
                for(int ii=1;ii<=9;ii++){// ox,oy,hor
                    for(int jj=1;jj<=9;jj++)vis[ii][jj]=false;
                }
                b1=con[i][j][3];b2=con[i][j+1][2];
                b3=con[i+1][j][3];b4=con[i+1][j+1][2];
                con[i][j][3]=con[i][j+1][2]=false;
                con[i+1][j][3]=con[i+1][j+1][2]=false;
                vis[i][j]=true;
                dfs(ox,oy);
                con[i][j][3]=b1;con[i][j+1][2]=b2;
                con[i+1][j][3]=b3;con[i+1][j+1][2]=b4;
                c=0;
                for(int k=1;k<=9;k++)if(vis[k][1])c++;
                if(c==0)possiblept[i][j][0]=false;
                //if(c==0)Toast.makeText(this,"wtf??",Toast.LENGTH_SHORT).show();
                ///////////////////////////////////
                for(int ii=1;ii<=9;ii++){//ox,oy, ver
                    for(int jj=1;jj<=9;jj++)vis[ii][jj]=false;
                }
                b1=con[i][j][0];b2=con[i+1][j][1];
                b3=con[i][j+1][0];b4=con[i+1][j+1][1];
                con[i][j][0]=con[i+1][j][1]=false;
                con[i][j+1][0]=con[i+1][j+1][1]=false;
                vis[i][j]=true;
                dfs(ox,oy);
                con[i][j][0]=b1;con[i+1][j][1]=b2;
                con[i][j+1][0]=b3;con[i+1][j+1][1]=b4;
                c=0;
                for(int k=1;k<=9;k++)if(vis[k][1])c++;
                if(c==0)possiblept[i][j][0]=false;
                //if(c==0)Toast.makeText(this,"wtf??",Toast.LENGTH_SHORT).show();
                ///////////////////////////////
                if(walls[i][j]>0)possiblept[i][j][0]=possiblept[i][j][1]=false;
                if(walls[i+1][j]==1||walls[i-1][j]==1)possiblept[i][j][0]=false;
                if(walls[i][j-1]==2||walls[i][j+1]==2)possiblept[i][j][1]=false;
            }
        }
    }
    void dfs(int x,int y){
        for(int i=0;i<4;i++){
            int tx=x+dx[i],ty=y+dy[i];
            if(vis[tx][ty])continue;
            if(!con[x][y][i])continue;
            vis[tx][ty]=true;
            dfs(tx,ty);
        }
    }
    void updatecellpossibilities(){
        pcells.clear();
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                possiblecell[i][j]=false;
            }
        }
        for(int i=0;i<4;i++){
            if(!con[px][py][i])continue;
            if(ox==px+dx[i]&&oy==py+dy[i]){
                if(!con[ox][oy][i]){
                    for(int k=0;k<2;k++){
                        if(con[ox][oy][orth[i]+k]){
                            possiblecell[ox+dx[orth[i]+k]][oy+dy[orth[i]+k]]=true;
                            pcells.add(new Point(ox+dx[orth[i]+k],oy+dy[orth[i]+k]));
                        }
                    }
                }
                else{
                    possiblecell[ox+dx[i]][oy+dy[i]]=true;
                    pcells.add(new Point(ox+dx[i],oy+dy[i]));
                }
            }
            else{
                possiblecell[px+dx[i]][py+dy[i]]=true;
                pcells.add(new Point(px+dx[i],py+dy[i]));
            }
        }
    }
    void hideallcontrolpanel(){
        choose_wall.setVisibility(View.GONE);
        choose_move.setVisibility(View.GONE);
        confirm_move.setVisibility(View.GONE);
        confirm_wall.setVisibility(View.GONE);
        wall_back.setVisibility(View.GONE);
        move_back.setVisibility(View.GONE);
        togglebtn.setVisibility(View.GONE);
        gotomenu.setVisibility(View.GONE);
    }
    void showbasiccontrolpanel(){
        choose_move.setVisibility(View.VISIBLE);
        if(leftwall>0)choose_wall.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(!isgameover)showMessage();
    }

    void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("기권하시겠습니까? 1패가 추가됩니다");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handlewaive("waive");
                isgameover=true;
                Intent intent = new Intent(gameboard.this, MenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void handlewaive(String detail){
//        Toast.makeText(this,"handlewaive",Toast.LENGTH_SHORT).show();
        if(mSocket!=null){
//            Toast.makeText(this,"Not null",Toast.LENGTH_SHORT).show();
        }
        mSocket.emit("waive",gson.toJson(new MessageData(
                id,
                roomnumber,
                opponentname,
                detail,
                ""
        )));
        CountDownTimer temptimer = new CountDownTimer(3, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(mSocket!=null)mSocket.disconnect();
            }
        }.start();
    }

    void timeover(){
        if(isgameover)return;
        constly.removeAllViews();
        leftwallandtimer.removeAllViews();
        ImageView resultimg = new ImageView(gameboard.this);

        LinearLayout.LayoutParams customparams = new LinearLayout.LayoutParams(scrx,scry*8/10);
        resultimg.setLayoutParams(customparams);
        resultimg.setImageResource(R.drawable.game_over);
        constly.addView(resultimg);
        hideallcontrolpanel();
        gotomenu.setVisibility(View.VISIBLE);
        isgameover=true;
        handlewaive("timeover");
    }
}