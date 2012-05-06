package be.hehehe.geekbot.persistence.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class QuizzMergeRequest implements Serializable {

	private static final long serialVersionUID = -8323718097566901796L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private QuizzPlayer player1;
	@ManyToOne
	private QuizzPlayer player2;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public QuizzPlayer getPlayer1() {
		return player1;
	}

	public void setPlayer1(QuizzPlayer player1) {
		this.player1 = player1;
	}

	public QuizzPlayer getPlayer2() {
		return player2;
	}

	public void setPlayer2(QuizzPlayer player2) {
		this.player2 = player2;
	}

}
