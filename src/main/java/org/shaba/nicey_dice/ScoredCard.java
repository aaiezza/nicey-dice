package org.shaba.nicey_dice;

@lombok.ToString ( callSuper = true )
@lombok.EqualsAndHashCode ( callSuper = true )
@lombok.Value
public class ScoredCard extends Card
{
    private ScoredCard( final FieldCard fieldCard )
    {
        super( fieldCard );
    }

    public static ScoredCard scoredCard( final FieldCard fieldCard )
    {
        return new ScoredCard( fieldCard );
    }
}
