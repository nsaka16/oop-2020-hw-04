import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CrackerTests {

    @Test
    public void testGenerationModeForString_Molly(){
        Cracker cracker = new Cracker();
        assertEquals("4181eecbd7a755d19fdf73887c54837cbecf63fd",cracker.generateHashCodeOfString("molly"));
    }

    @Test
    public void testGenerationModeForString_a(){
        Cracker cracker = new Cracker();
        assertEquals("34800e15707fae815d7c90d49de44aca97e2d759",cracker.generateHashCodeOfString("a!"));
    }

    @Test
    public void testGenerationModeForString_xyz(){
        Cracker cracker = new Cracker();
        assertEquals("66b27417d37e024c46526c2f6d358a754fc552f3",cracker.generateHashCodeOfString("xyz"));
    }

    @Test
    public void testCrackingMode_molly() throws InterruptedException {
        Cracker cracker = new Cracker();
        assertEquals("molly",cracker.crackGivenHashCode( Cracker.hexToArray("4181eecbd7a755d19fdf73887c54837cbecf63fd"),5,8));
    }

    @Test
    public void testCrackingMode_a() throws InterruptedException {
        Cracker cracker = new Cracker();
        assertEquals("a!",cracker.crackGivenHashCode( Cracker.hexToArray("34800e15707fae815d7c90d49de44aca97e2d759"),5,8));
    }

    @Test
    public void testCrackingMode_xyz() throws InterruptedException {
        Cracker cracker = new Cracker();
        assertEquals("xyz",cracker.crackGivenHashCode( Cracker.hexToArray("66b27417d37e024c46526c2f6d358a754fc552f3"),5,8));
    }

    @Test
    public void testPasswordEqualsHash_molly(){
        Cracker cracker = new Cracker();
        assertTrue(cracker.passedPasswordEqualsToHash("molly",Cracker.hexToArray("4181eecbd7a755d19fdf73887c54837cbecf63fd")));
    }

    @Test
    public void testPasswordEqualsHash_xyz(){
        Cracker cracker = new Cracker();
        assertTrue(cracker.passedPasswordEqualsToHash("xyz",Cracker.hexToArray("66b27417d37e024c46526c2f6d358a754fc552f3")));
    }

    @Test
    public void testFindPassword_1(){
        Cracker cracker = new Cracker();
        List<String> result = new ArrayList<>();
        cracker.findPasswordRecursiveHelper("a",Cracker.hexToArray("34800e15707fae815d7c90d49de44aca97e2d759"),result,2);
        assertEquals("a!",result.get(0));
    }

    @Test
    public void testFindPassword_2(){
        Cracker cracker = new Cracker();
        List<String> result = new ArrayList<>();
        cracker.findPasswordRecursiveHelper("x",Cracker.hexToArray("66b27417d37e024c46526c2f6d358a754fc552f3"),result,3);
        assertEquals("xyz",result.get(0));
    }

    @Test
    public void testFindPassword_3(){
        Cracker cracker = new Cracker();
        List<String> result = new ArrayList<>();
        cracker.findPasswordRecursiveHelper("m",Cracker.hexToArray("4181eecbd7a755d19fdf73887c54837cbecf63fd"),result,5);
        assertEquals("molly",result.get(0));
    }
    
}
