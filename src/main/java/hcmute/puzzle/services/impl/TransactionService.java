package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.ServerException;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.entities.Package;
import hcmute.puzzle.infrastructure.repository.TransactionHistoryRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionService {
	@Autowired
	TransactionHistoryRepository transactionHistoryRepository;

	@Autowired
	UserRepository userRepository;

	public DataResponse subscribePackage(User user, Package aPackage, Invoice invoice) {
		if (aPackage.getCoin() == null) {
			throw new ServerException("This package is currently having problems, please try again later");
		}
		Long newBalance = user.getBalance() + aPackage.getCoin();
		TransactionHistory transactionHistory = TransactionHistory.builder()
				.prevBalance(user.getBalance())
				.newBalance(newBalance)
				.balanceChange(newBalance - user.getBalance())
				.description("Pay for increase balance")
				.paymentTransactionCode(invoice.getTransactionCode())
		.build();
		user.setBalance(newBalance);
		// Hoá đơn đã lưu trong db rồi thì mới set
		transactionHistoryRepository.save(transactionHistory);

		userRepository.save(user);
		return null;
	}
}
