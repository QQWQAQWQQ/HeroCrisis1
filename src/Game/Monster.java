package Game;

import java.awt.*;
import java.lang.annotation.Target;

public class Monster extends Enemy {
    //怪物(僵尸)的属性
    public Monster(int x, int y, World world){
        super("Monster", 170, 14, 2, x, y, world);
        //设置手为武器
        addWeapon(new Hand(this, world));
        //设置没有武器
        setCurrentWeapon(getWeapons().get(0));
        //设置伤害
        getCurrentWeapon().setDamage(100);
        //攻击冷却时间
        getCurrentWeapon().setColdDownTime(24);
        //设置攻击范围
        ((Hand)getCurrentWeapon()).setAttackRange(50);
    }

    //画出怪兽
    public void draw(Graphics g) {
        //怪物不为空或hp>0
        if(getTarget() != null && getTarget().getHP() > 0) {
            //得到坐标
            int distance = (int) getDistance(this.getX(), this.getY(), getCurrentTarget().getX(), getCurrentTarget().getY());
            //如果攻击距离比僵尸距离主角的距离远就往主角哪里走 如果距离够得到就用手攻击
            if (distance <= ((Hand) getCurrentWeapon()).getAttackRange() && this.getCurrentWeapon().getColdDown() == 0) {
                Direction dir = judgeAccurateDir(getTarget().getX(), getTarget().getY());
                this.dir = dir;
                this.oldDir = (dir == Direction.STOP ? oldDir : dir);
                this.getCurrentWeapon().setState();
                this.getCurrentWeapon().setColdDown();
                getPath();
            }
        }
        locateDirection();
        super.draw(g);
    }


}
