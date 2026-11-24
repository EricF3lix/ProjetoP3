package repositories;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public class ManipulaArquivo {

	private static final String CAMPO_ID = "id";

	private <T> Path<?> resolverCaminho(Root<T> root, String atributo) {
		Path<?> caminho = root;
		for (String parte : atributo.split("\\.")) {
			caminho = caminho.get(parte);
		}
		return caminho;
	}

	private <T> List<T> executarConsulta(EntityManager em, CriteriaQuery<T> criteriaQuery) {
		return new ArrayList<>(em.createQuery(criteriaQuery).getResultList());
	}

	public <T, V> List<T> buscar(Class<T> tipo, String atributo, V valor) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(tipo);
			Root<T> root = cq.from(tipo);
			Path<?> caminho = resolverCaminho(root, atributo);

			String padrao = (valor + "%").toLowerCase();

			cq.select(root)
			  .where(cb.like(cb.lower(caminho.as(String.class)), padrao))
			  .orderBy(cb.asc(root.get(CAMPO_ID)));

			return executarConsulta(em, cq);
		} finally {
			em.close();
		}
	}

	public <T, V> List<T> buscaExata(Class<T> tipo, String atributo, V valor) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(tipo);
			Root<T> root = cq.from(tipo);
			Path<?> caminho = resolverCaminho(root, atributo);

			cq.select(root)
			  .where(cb.equal(caminho, valor))
			  .orderBy(cb.asc(root.get(CAMPO_ID)));

			return executarConsulta(em, cq);
		} finally {
			em.close();
		}
	}

	public <T> T buscaUnica(Class<T> tipo, int id) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.find(tipo, id);
		} finally {
			em.close();
		}
	}

	public <T, V> boolean atualizar(Class<T> tipo, String atributo, V valor, int id) {
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction et = em.getTransaction();

		try {
			et.begin();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<T> cu = cb.createCriteriaUpdate(tipo);
			Root<T> root = cu.from(tipo);

			cu.set(atributo, valor)
			  .where(cb.equal(root.get(CAMPO_ID), id));

			int linhasAfetadas = em.createQuery(cu).executeUpdate();
			et.commit();
			return linhasAfetadas > 0;
		} catch (RuntimeException e) {
			if (et.isActive()) {
				et.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public <T> boolean desativar(Class<T> tipo, int id) {
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction et = em.getTransaction();

		try {
			et.begin();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<T> cu = cb.createCriteriaUpdate(tipo);
			Root<T> root = cu.from(tipo);

			cu.set("ativo", false)
			  .where(cb.equal(root.get(CAMPO_ID), id));

			int linhasAfetadas = em.createQuery(cu).executeUpdate();
			et.commit();
			return linhasAfetadas > 0;
		} catch (RuntimeException e) {
			if (et.isActive()) {
				et.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public <T> boolean escrever(T tipo) {
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction et = em.getTransaction();

		try {
			et.begin();
			em.persist(tipo);
			et.commit();
			return true;
		} catch (RuntimeException e) {
			if (et.isActive()) {
				et.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public <T> List<T> carregarTudo(Class<T> tipo) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(tipo);
			Root<T> root = cq.from(tipo);

			cq.select(root)
			  .orderBy(cb.asc(root.get(CAMPO_ID)));

			return executarConsulta(em, cq);
		} finally {
			em.close();
		}
	}
}