package repositories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {

private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("loja-aluga-de-um-tudo");

private JPAUtil() {
}

public static EntityManager getEntityManager() {
	return emf.createEntityManager();
}
	
}
