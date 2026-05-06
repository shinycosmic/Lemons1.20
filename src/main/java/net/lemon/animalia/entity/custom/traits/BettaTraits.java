package net.lemon.animalia.entity.custom.traits;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.units.qual.A;

import java.awt.*;

public class BettaTraits {
    public enum PatternPreset {
        SOLID(0),
        BICOLOR(1),
        CAMBODIAN(2),
        BUTTERFLY(3),
        MARBLE(4),
        PIEBALD(5),
        MULTICOLOR(6),
        DRAGON(7);

        private final int id;

        PatternPreset(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PatternPreset fromId(int id) {
            for (PatternPreset p : values()) {
                if (p.id == id) return p;
            }
            return SOLID;
        }
    }

    public enum CaudalPreset {
        VEIL(0),
        CROWN(1),
        COMB(2),
        HM(3),
        DOUBLE(4),
        ROSE(5),
        PLAKAT(6),
        SPADE(7);

        private final int id;

        CaudalPreset(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static CaudalPreset fromId(int id) {
            for (CaudalPreset p : values()) {
                if (p.id == id) return p;
            }
            return VEIL;
        }
    }

    public enum DorsalPreset {
        TALL(0),
        MEDIUM(1),
        WILD(2);

        private final int id;

        DorsalPreset(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static DorsalPreset fromId(int id) {
            for (DorsalPreset p : values()) {
                if (p.id == id) return p;
            }
            return TALL;
        }
    }

    public enum AnalPreset {
        TALL(0),
        MEDIUM(1),
        SHORT(2),
        WILD(3);

        private final int id;

        AnalPreset(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static AnalPreset fromId(int id) {
            for (AnalPreset p : values()) {
                if (p.id == id) return p;
            }
            return TALL;
        }
    }

    public enum PelvicPreset {
        TALL(0),
        MEDIUM(1),
        SHORT(2),
        TINY(3);

        private final int id;

        PelvicPreset(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PelvicPreset fromId(int id) {
            for (PelvicPreset p : values()) {
                if (p.id == id) return p;
            }
            return TALL;
        }
    }

    public enum BodyPreset {
        SOLID(0),
        DRAGON(1),
        MARBLE(2),
        PIEBALD(3);

        private final int id;

        BodyPreset(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static BodyPreset fromId(int id) {
            for (BodyPreset p : values()) {
                if (p.id == id) return p;
            }
            return SOLID;
        }
    }

    // Colors
    public ColorUtil primaryColor;
    public final ColorUtil secondaryColor;
    public ColorUtil thirdColor;

    // Pattern
    public final PatternPreset patternPreset;

    /**
     * All fins will be set to butterfly version if is butterfly.
     * This is a passed down trait.
     * Exception: if is Multicolor or Butterfly -> set to true no matter what
     * If is Solid, Bicolor, Cambodian, or Marble -> set to false
     * if is Piebald or Dragon -> keep inherited
     */
    public boolean isButterfly;

    // Model Presets
    public final CaudalPreset caudalPreset;
    public final DorsalPreset dorsalPreset;
    public final AnalPreset analPreset;
    public final PelvicPreset pelvicPreset;
    public BodyPreset bodyPreset;

    // Special textures
    public boolean isSpecialVariant = false;
    public String specialTexture;

    public BettaTraits(
            ColorUtil primaryColor,
            ColorUtil secondaryColor,
            ColorUtil thirdColor,
            PatternPreset patternPreset,
            boolean isButterfly,
            BodyPreset bodyPreset,
            DorsalPreset dorsalPreset,
            CaudalPreset caudalPreset,
            AnalPreset analPreset,
            PelvicPreset pelvicPreset
    ) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.thirdColor = thirdColor;
        this.patternPreset = patternPreset;
        this.isButterfly = isButterfly;
        this.bodyPreset = bodyPreset;
        this.dorsalPreset = dorsalPreset;
        this.caudalPreset = caudalPreset;
        this.analPreset = analPreset;
        this.pelvicPreset = pelvicPreset;
    }

    /***
     * resolve attributes within the object
     */
    public void resolveAttributes(){
        switch (this.patternPreset){
            case SOLID, BICOLOR:
                this.bodyPreset = BettaTraits.BodyPreset.SOLID;
                this.isButterfly = false;
                break;
            case MULTICOLOR:
                this.bodyPreset = BettaTraits.BodyPreset.MARBLE;
                this.isButterfly = true;
                if(this.thirdColor == ColorUtil.NONE) this.thirdColor = this.primaryColor;
                break;
            case CAMBODIAN:
                this.bodyPreset = BettaTraits.BodyPreset.SOLID;
                this.primaryColor = ColorUtil.WHITE;
                break;
            case MARBLE:
                this.bodyPreset = BettaTraits.BodyPreset.MARBLE;
                if(this.thirdColor == ColorUtil.NONE) this.thirdColor = this.primaryColor;
                break;
            case BUTTERFLY:
                this.bodyPreset = BettaTraits.BodyPreset.SOLID;
                this.isButterfly = true;
                break;
            case PIEBALD:
                this.bodyPreset = BettaTraits.BodyPreset.PIEBALD;
                this.primaryColor = ColorUtil.WHITE;
                break;
            case DRAGON:
                this.bodyPreset = BettaTraits.BodyPreset.DRAGON;
                break;
        }
    }


}
