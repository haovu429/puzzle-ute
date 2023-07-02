package hcmute.puzzle.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction_history")
public class TransactionHistory extends Auditable implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column(name = "prev_balance")
	private Long prevBalance;

	@Column(name = "new_balance")
	private Long newBalance;

	@Column(name = "balance_change")
	private Long balanceChange;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "payment_transaction_code", columnDefinition = "VARCHAR(30)")
	private String paymentTransactionCode;
}
