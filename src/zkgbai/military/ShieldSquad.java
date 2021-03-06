package zkgbai.military;

import com.springrts.ai.oo.AIFloat3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aeonios on 11/29/2015.
 */
public class ShieldSquad extends Squad{
    static int CMD_ORBIT = 13923;
    static int CMD_ORBIT_DRAW = 13924;
    static short OPTION_SHIFT_KEY = (1 << 5);
    private Fighter leader;
    private int leaderWeight;

    ShieldSquad(){
        super();
        this.leader = null;
        this.leaderWeight = 0;
    }

    @Override
    public void addUnit(Fighter f, int frame){
        f.squad = this;
        metalValue = metalValue + f.metalValue;

        if (leader == null){
            f.getUnit().setMoveState(1, (short) 0, frame+300);
            leader = f;
            leaderWeight = getUnitWeight(f);
            f.fightTo(target, frame);
        }else if (getUnitWeight(f) > leaderWeight){
            f.getUnit().setMoveState(1, (short) 0, frame+300);
            leader.getUnit().setMoveState(0, (short) 0, frame + 300);
            fighters.add(leader);
            leader = f;
            leaderWeight = getUnitWeight(f);
            leader.fightTo(target, frame);
            List<Float> params = new ArrayList<>();
            List<Float> drawParams = new ArrayList<>();
            params.add((float)leader.id);
            drawParams.add((float)leader.id);
            params.add(50f);
            for (Fighter fi: fighters){
                fi.getUnit().executeCustomCommand(CMD_ORBIT, params, (short) 0, frame + 3000);
                fi.getUnit().executeCustomCommand(CMD_ORBIT_DRAW, drawParams, OPTION_SHIFT_KEY, frame + 3000);
            }
        }else{
            f.getUnit().setMoveState(0, (short) 0, frame+300);
            fighters.add(f);
            List<Float> params = new ArrayList<>();
            List<Float> drawParams = new ArrayList<>();
            params.add((float)leader.id);
            drawParams.add((float)leader.id);
            params.add(50f);
            f.getUnit().executeCustomCommand(CMD_ORBIT, params, (short) 0, frame + 3000);
            f.getUnit().executeCustomCommand(CMD_ORBIT_DRAW, drawParams, OPTION_SHIFT_KEY, frame + 3000);
        }
    }

    @Override
    public void removeUnit(Fighter f){
        if (leader.equals(f)){
            metalValue -= f.metalValue;
            leader = getNewLeader();
            if (leader == null){
                leaderWeight = 0;
                return;
            }
            fighters.remove(leader);
            leaderWeight = getUnitWeight(leader);
            leader.getUnit().setMoveState(1, (short) 0, Integer.MAX_VALUE);
            target = null;
            List<Float> params = new ArrayList<>();
            List<Float> drawParams = new ArrayList<>();
            params.add((float)leader.id);
            drawParams.add((float)leader.id);
            params.add(50f);
            for (Fighter fi:fighters){
                fi.getUnit().executeCustomCommand(CMD_ORBIT, params, (short) 0, Integer.MAX_VALUE);
                fi.getUnit().executeCustomCommand(CMD_ORBIT_DRAW, drawParams, OPTION_SHIFT_KEY, Integer.MAX_VALUE);
            }
        }else{
            super.removeUnit(f);
        }
    }

    @Override
    public void setTarget(AIFloat3 pos, int frame){
        // set a target for the squad to attack.
        target = pos;
        if (leader != null && leader.getUnit().getHealth() > 0) {
            leader.fightTo(target, frame);
        }
    }

    @Override
    public void retreatTo(AIFloat3 pos, int frame){
        // set a target for the squad to attack.
        target = pos;
        if (leader != null && leader.getUnit().getHealth() > 0) {
            leader.moveTo(target, frame);
        }
    }

    @Override
    public AIFloat3 getPos(){
        if (leader != null){
            return leader.getPos();
        }
        return target;
    }

    @Override
    public boolean isDead(){
        return (fighters.isEmpty() && leader == null);
    }

    public float getHealth(){
        if (leader == null){
            return 0;
        }

        float count = fighters.size() + 1;
        float health = 0;
        health += (leader.getUnit().getHealth()/leader.getUnit().getMaxHealth())/count;

        for (Fighter f:fighters){
            health += (f.getUnit().getHealth()/f.getUnit().getMaxHealth())/count;
        }

        return health;
    }

    Fighter getNewLeader(){
        Fighter bestFighter = null;
        int score = 0;
        for (Fighter f: fighters){
            int tmpscore = getUnitWeight(f);
            if (tmpscore > score){
                score = tmpscore;
                bestFighter = f;
            }
        }
        return bestFighter;
    }

    int getUnitWeight(Fighter f){
        String type = f.getUnit().getDef().getName();
        switch (type){
            case "funnelweb": return 1; // funnels skimrmish from too large a range for other units to do any damage
            case "cormak": return 2; // outlaws are too fast for other units to keep up with
            case "shieldarty": return 3;
            case "corthud": return 4;
            case "shieldfelon": return 5;
        }
        return 0;
    }
}
