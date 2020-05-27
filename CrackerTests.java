import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrackerTests {

    @Test
    public void testGenerationModeForString_Molly(){
        Cracker cracker = new Cracker();
        assertEquals("4181eecbd7a755d19fdf73887c54837cbecf63fd",cracker.generateHashOfString("molly"));
    }

    @Test
    public void testGenerationModeForString_a(){
        Cracker cracker = new Cracker();
        assertEquals("34800e15707fae815d7c90d49de44aca97e2d759",cracker.generateHashOfString("a!"));
    }

    @Test
    public void testGenerationModeForString_xyz(){
        Cracker cracker = new Cracker();
        assertEquals("66b27417d37e024c46526c2f6d358a754fc552f3",cracker.generateHashOfString("xyz"));
    }

//    @Test
//    public void testCrackingMode_molly(){
//        Cracker cracker = new Cracker();
//        assertEquals("molly",cracker.crackHashCodeOfArbitraryPassword( "4181eecbd7a755d19fdf73887c54837cbecf63fd",5,8).get(0));
//    }
//
//    @Test
//    public void testCrackingMode_a(){
//        Cracker cracker = new Cracker();
//        assertEquals("a!",cracker.crackHashCodeOfArbitraryPassword( "34800e15707fae815d7c90d49de44aca97e2d759",5,8).get(0));
//    }
//
//    @Test
//    public void testCrackingMode_xyz(){
//        Cracker cracker = new Cracker();
//        assertEquals("xyz",cracker.crackHashCodeOfArbitraryPassword( "66b27417d37e024c46526c2f6d358a754fc552f3",5,8).get(0));
//    }


}
