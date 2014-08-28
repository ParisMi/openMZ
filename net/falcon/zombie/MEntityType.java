package net.falcon.zombie;

import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityPigZombie;
import net.minecraft.server.v1_7_R3.EntityZombie;

import org.bukkit.entity.EntityType;

public enum MEntityType {
	 
    MZOMBIE("Zombie", 54, EntityType.ZOMBIE, EntityZombie.class, MZombie.class),	
    MPIG_ZOMBIE("PigZombie", 57, EntityType.PIG_ZOMBIE, EntityPigZombie.class, MPigZombie.class);
    
    private int id;
    private String name;
    private EntityType entityType;
    private Class<? extends EntityInsentient> nmsClass;
    private Class<? extends EntityInsentient> customClass;
 
    private MEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass){
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }
 
    public String getName(){
        return this.name;
    }
 
    public int getID(){
        return this.id;
    }
 
    public EntityType getEntityType(){
        return this.entityType;
    }
 
    public Class<? extends EntityInsentient> getNMSClass(){
        return this.nmsClass;
    }
 
    public Class<? extends EntityInsentient> getCustomClass(){
        return this.customClass;
    }
 
}