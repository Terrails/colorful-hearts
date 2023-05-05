# Colorful Stats
A client side mod that replaces vanilla multiple heart rows with a single row using colored hearts.

CurseForge: https://www.curseforge.com/minecraft/mc-mods/colorful-hearts  
Modrinth: https://modrinth.com/mod/colorful-hearts

Instead of vanilla behavior the mod uses colored hearts to represent each row of health. The colors can be configured with a simple config file that accepts a list of hex `#RRGGBB` color values.

Also adds half heart bordered background textures. This means that if there is only half a heart of absorption, it will not have a black background on the right as if it there is something missing. Instead the black bordered background will only cover the left half of the heart icon.

While the mod replicates default vanilla behavior of absorption being rendered a row above health, there is a toggle that instead moves absorption to the same row as health. Absorption icons then start to render in the same way as health.

### Resource Packs
Due to user configurable heart colors, textures have to be in layers instead of a single texture like with vanilla hearts. If anyone wants to create a resource pack for this mod there are a few things that have to be known.
- Hearts are drawn in 3 layers.
    - Heart icon by itself, fully white with little shading. Colored in code.
    - Tiny white dot. Also present in vanilla hearts.
        - Transparent in order to absorb a bit of the color from the first layer
    - Shading. Darker area present at the bottom of vanilla hearts
        - Also transparent in order darken the existing color from the first layer

Details can be seen below.

## Textures
Visible in `common/src/main/resources/assets/colorfulhearts/textures`

###### half_heart.png
- Used for half heart absorptions or when max health is lowered below 20
- Dark value is the standard and white value is displayed when health is regenerating

###### absorption.png & health.png
- Absorption and health hearts colored via config values
- First row of hearts is used in non-hardcore and second in hardcore worlds
    - From left to right: standard, poisoned, withered, frozen
    - 1st row below hearts is used to add a tiny dot
    - 2nd row below hearts is used to add shading
- If making a custom resource pack, make sure that the dot and shading are transparent if darkening/lightening effect of color is wanted.
    - Opacity/alpha for dots in default textures
        - normal: 216/255 or 85%
        - hardcore
            - health: 178/255 or 70%
            - absorption: 88/255 or 35%
    - Opacity/alpha for shading in default textures
        - withered: 216/255 or 85%
        - other: 56/255 or 22%