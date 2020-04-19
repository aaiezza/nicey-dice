package org.shaba.nicey_dice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.shaba.nicey_dice.Card.card;
import static org.shaba.nicey_dice.DiceFace.dF;
import static org.shaba.nicey_dice.DiceFace.diceFaces;
import static org.shaba.nicey_dice.Points.points;

import org.junit.Test;

public class CardTest
{
    private DiceFace [] diceFaces;
    private Card        subject;

    @Test
    public void given44Card_pointsAre2()
    {
        subject = card( dF( 4 ), dF( 4 ) );

        assertThat( subject.getPoints() ).isEqualTo( points( 2 ) );
    }

    @Test
    public void given123Card_pointsAre5()
    {
        subject = card( dF( 1 ), dF( 2 ), dF( 3 ) );

        assertThat( subject.getPoints() ).isEqualTo( points( 5 ) );
    }

    @Test
    public void given5321Card_pointsAre10()
    {
        diceFaces = diceFaces( 5, 3, 2, 1 );

        subject = card( diceFaces );

        assertThat( subject.getPoints() ).isEqualTo( points( 10 ) );
    }

    @Test
    public void given333333Card_pointsAre15()
    {
        diceFaces = diceFaces( 3, 3, 3, 3, 3, 3 );

        subject = card( diceFaces );

        assertThat( subject.getPoints() ).isEqualTo( points( 15 ) );
    }

    @Test
    public void givenEmptyCard_exceptionThrown()
    {
        assertThatThrownBy( () -> subject = card() )
                .isExactlyInstanceOf( IllegalArgumentException.class );
    }

    @Test
    public void givenNullDiceFacesForCard_exceptionThrown()
    {
        assertThatThrownBy( () -> subject = card( diceFaces ) )
                .isExactlyInstanceOf( NullPointerException.class );
    }

    @Test
    public void given5Card_exceptionThrown()
    {
        diceFaces = diceFaces( 5 );
        assertThatThrownBy( () -> subject = card( diceFaces ) )
                .isExactlyInstanceOf( IllegalArgumentException.class );
    }

    @Test
    public void given12345Card_exceptionThrown()
    {
        diceFaces = diceFaces( 1, 2, 3, 4, 5 );
        assertThatThrownBy( () -> subject = card( diceFaces ) )
                .isExactlyInstanceOf( IllegalArgumentException.class );
    }

    @Test
    public void given1234561Card_exceptionThrown()
    {
        diceFaces = diceFaces( 1, 2, 3, 4, 5, 6, 1 );
        assertThatThrownBy( () -> subject = card( diceFaces ) )
                .isExactlyInstanceOf( IllegalArgumentException.class );
    }
}
