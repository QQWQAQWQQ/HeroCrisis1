package Game;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Role extends GameObject{
    public static final int PICOFFSET = 32;
    private int walkState; //行走状态
    private List<Weapon> weapons;//武器列表
    private Weapon currentWeapon;//现在的武器
    protected int deadState;//死亡状态
    private int maxHP;//满血
    private int begin;//开始
    //血压
    private class BloodBar {
        //画出血液
        public void draw(Graphics g) {
            //最大血液长度
            int maxLength = 40;
            //
            int length = (int)((double)getHP() / (double)getMaxHP() * 40);
            Color c = g.getColor();
            //设置血液颜色为红色
            g.setColor(Color.RED);
            //画出矩形
            g.drawRect(x - 20, y - 40, maxLength, 7);
            //将血液颜色填充进矩形
            g.fillRect(x - 20, y - 40, length, 7);
            g.setColor(c);

        }
    }
    private BloodBar bloodBar;

    //基础规则的参数列表
    public Role(String name, int HP, int radius, int speed,int x, int y, World world) {
        super(name , radius, speed, HP, x, y, true, world);
        this.maxHP = this.HP;
        this.weapons = new ArrayList<>();
        this.walkState = 0;
        this.deadState = -1;
        if(this instanceof Hero)
        	this.begin = 250;
        else this.begin = 0;
        bloodBar = new BloodBar();
    }

    //判断行走改变方向什么的
    public int mainTainWalkState(int n){
        //如果不是静止状态 老方向等于新方向就行走一单位 如果不等于就改变方向不行走
        if(dir != Direction.STOP) {
            if (dir == oldDir) {
                walkState++;
            } else {
                walkState = 0;//保持坐标不变
                oldDir = dir;//原方向和新方向相同+
            }
        } else {
            walkState = -1;//后退一步
        }
        if(walkState >= n) walkState = 0;
        return walkState;
    }

    //画出行走图片
    public void drawWalkImage(Graphics g) {
        if(mainTainWalkState(16) < 0){
            drawOneImage(g, name, PICOFFSET,this.x, this.y, 0, this.oldDir.ordinal());
        } else {
            drawOneImage(g, name, PICOFFSET, this.x, this.y, walkState / 4 + 1, this.dir.ordinal());
        }
    }

    //画笔画出图片
    public void draw(Graphics g) {
	    if(deadState >= 0){
	        this.drawOneImage(g, name, PICOFFSET, this.x, this.y, 13, 0);
	        this.maintainDeadState();
	        return;
	    }
	    //检测到攻击画出 伤害大于20添加血迹
	    if(checkOnAttack() > 0){
	        this.drawOneImage(g, name, PICOFFSET, this.x, this.y, 0, this.oldDir.ordinal());
	        Random rand = new Random();
	        if(Math.abs(rand.nextInt(100)) > 20) world.addBlood(this.x, this.y);
	        onAttackState--;
	    } else if (currentWeapon.getState() >= 0 && (currentWeapon instanceof Sword || currentWeapon instanceof Hand)) {
	        this.currentWeapon.drawNomalAttack(g);
	        if (this.currentWeapon.getState() == 1)
	            this.currentWeapon.Attack();
	        this.currentWeapon.maintainColdDown();
	        return;
	    }
	    this.drawWalkImage(g);
        
        this.currentWeapon.maintainColdDown();
        //g.drawString("("+String.valueOf(this.x) +","+ String.valueOf(this.y)+")", this.x-8, this.y - 40);
        move();
    }

    //移动方法
    public void move(){
        if((getDir() == Direction.STOP  && checkOnAttack() <= 0) || getHP() <= 0) return;
        double degree = Direction.toDegree(getDir());
        if(checkOnAttack() <= 0) {
            setxIncrement(degree, speed);
            setyIncrement(degree, speed);
        }
        world.collisionDetection(this);
        this.x += getxIncrement();
        this.y += getyIncrement();
    }

    //碰撞响应
    public void collisionResponse(GameObject object){
        //如果位置静止
        if(this.dir == Direction.STOP) return;

        int deltaX = object.getX() - this.getX();
        int deltaY = object.getY() - this.getY();
        double tmpVectorX = 1.0 / deltaX;
        double tmpVectorY = -1.0 / deltaY;
        double normOfTmp = Math.sqrt(Math.pow(tmpVectorX, 2) + Math.pow(tmpVectorY, 2));
        double dirX = Math.cos(Math.toRadians(Direction.toDegree(this.dir)));
        double dirY = -Math.sin(Math.toRadians(Direction.toDegree(this.dir)));
        double newSpeed = (tmpVectorX * dirX + tmpVectorY * dirY) / normOfTmp * getSpeed();
        int newDirX = (int) (newSpeed * tmpVectorX);
        int newDirY = (int) (newSpeed * tmpVectorY);
        this.x += newDirX;
        this.y += newDirY;
    }

    //如果攻击状态
    public void onAttack(Weapon weapon){
        //hp<=0
    	if(getHP() <= 0 || begin > 0) return;
        this.onAttackState = 5;
        this.setHP(this.getHP() - weapon.getDamage());
        if(getHP() <= 0) {
            this.setHP(0);
            this.setDeadState();
            return;
        }
        int weaponX = weapon.getX();
        int weaponY = weapon.getY();
        int deltaX = weaponX - this.x;
        int deltaY = weaponY - this.y;
        double D = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        double cosA = deltaX / D;
        double sinA = deltaY / D;
        this.xIncrement = (int)(-cosA * 8);
        this.yIncrement = (int)(-sinA * 8);
        world.addBlood(this.x, this.y);
    }

    public void setDeadState(){
        this.deadState = 150;
    }

    public int getWeaponsAmount(){
        return weapons.size();
    }

    public void addWeapon(Weapon weapon){
        weapons.add(weapon);
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }
    
    public void maintainDeadState() {
    	if(deadState > 0)
        	deadState--;
    	else
    		world.objDead(this);
    }

    public void NextWeapon() {
        int index = weapons.indexOf(currentWeapon);
        if(index + 1 < this.getWeaponsAmount()){
            currentWeapon = weapons.get(index + 1);
        } else {
            currentWeapon = weapons.get(0);
        }
    }

    public boolean collisionDetection(GameObject object){
        if(object instanceof Weapon && ((Weapon) object).isHost(this)){
            return false;
        }
        return super.collisionDetection(object);
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void drawBloodBar(Graphics g){
        bloodBar.draw(g);
    }
    
    public int getBegin() {
    	return begin--;
    }
    
    public void resetBegin() {
    	this.begin = 350;
    }
}