package net.lemon.animalia.entity.bases.helpers;

public interface IBold {

    boolean isBold();

    void setBold(boolean bold);

    default float boldChance() {
        return 0.5F;
    }
}