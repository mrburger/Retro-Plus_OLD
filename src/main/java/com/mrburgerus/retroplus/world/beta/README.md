# Burger's Beta Notes & Ramblings

## SurfaceChunkGenerator.java
### Fields
* Inputs include two fields of possible interest in noise resolution, both `ints`
    * `verticalNoiseResolution`
    * `horizontalNoiseResolution`
* There are 3 fields for Perlin Noise "Samplers", could be of use to emulate
    * All are of type `OctavePerlinNoiseSampler`, which is complicated.
        * Drilling down, looks like the former Beta+ 'AbstractPerlinGenerator' and subclasses contain **VERY** similar declarations
        * Could be that these classes are simply "optimized" Beta classes, since
        Perlin Noise is always generated with a style. The methods are really hard to trace.
* `OctavePerlinNoiseSampler` looks to be the same calculations as the legacy Beta+
    * Conversion of input and output will need to be done
* OR, I could just re-use and modify my previously created methods, calling them also for height maps.