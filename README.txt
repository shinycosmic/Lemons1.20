
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "un-renamed" MCP source code (aka
SRG Names) - this means that you will not be able to read them directly against
normal code.

Setup Process:
==============================

Adding nonspecial Items:
- AnimaliaFoodTab (Creative Tab)
- ModFoods/ModItems (registry)
- ModItemModelProvider/ModItemTagGenerator (datagen)
- ModRecipeProvider (datagen for recipes)
- en_us.json (lang)
- run runData to gen jsons

Special items prob need their own java classes in addition to the above

Adding Entities:
- ModEntities (registry)
- AnimaliaMobsTab (creativetab)
- ModEventBusEvents (registerAttributes, events)
- NameEntity (Entityclass, in entity -> Custom)
- NameRender (RenderClass, in entity -> render)
- NameModel (ModelClass, in entity -> model)
- AnimaliaRenderInit (init the renderer in util)
- add geo.json to assets -> geo
- add animation file to assets -> animations
- add texture file to assets ->textures -> entity

May need new AI or Bases with the above

Mapping Names:
=============================
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources: 
=========================
Community Documentation: https://docs.minecraftforge.net/en/1.20.1/gettingstarted/
LexManos' Install Video: https://youtu.be/8VEdtQLuLO0
Forge Forums: https://forums.minecraftforge.net/
Forge Discord: https://discord.minecraftforge.net/
