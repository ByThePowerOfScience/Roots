### Examples

```zenscript
import mods.roots.predicates.BlockStateBelow;
import mods.roots.predicates.PropertyPredicate;

BlockStateBelow.create(PropertyPredicate.create(<blockstate:minecraft:log:variant=spruce>, "variant")); // Creates a world predicate that will only match blocks which have a block below them that is both a log and with the variant of spruce.
```

### Notes

A positional IWorldPredicate that allows for the specification of an IPredicate to check above blocks for. 
