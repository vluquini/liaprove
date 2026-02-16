package com.lia.liaprove.infrastructure.configs;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.metrics.*;
import com.lia.liaprove.application.services.question.*;
import com.lia.liaprove.application.services.user.*;
import com.lia.liaprove.core.usecases.metrics.*;
import com.lia.liaprove.core.usecases.question.*;
import com.lia.liaprove.core.usecases.user.admin.UserModerationUseCase;
import com.lia.liaprove.core.usecases.user.users.*;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.VoteMapper;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.VoteJpaRepository;
import com.lia.liaprove.infrastructure.services.FeedbackGatewayImpl;
import com.lia.liaprove.infrastructure.services.PasswordHasherImpl;
import com.lia.liaprove.infrastructure.services.UserGatewayImpl;
import com.lia.liaprove.infrastructure.services.VoteGatewayImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableScheduling
public class AppConfig {

    // Auth
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordHasher passwordHasher(PasswordEncoder passwordEncoder) {
        return new PasswordHasherImpl(passwordEncoder);
    }

    // User Domain
    @Bean
    public UserGateway userGateway(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        return new UserGatewayImpl(userJpaRepository, userMapper);
    }

    @Bean
    public UserFactory userFactory() {
        return new DefaultUserFactory();
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordHasher passwordHasher,
                                               UserFactory userFactory) {
        return new CreateUserUseCaseImpl(userGateway, passwordHasher, userFactory);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(UserGateway userGateway) {
        return new GetUserByIdUseCaseImpl(userGateway);
    }

    @Bean
    public FindUsersUseCase findUsersUseCase(UserGateway userGateway) {
        return new FindUsersUseCaseImpl(userGateway);
    }

    @Bean
    public UpdateUserProfileUseCase updateUserProfileUseCase(UserGateway userGateway) {
        return new UpdateUserProfileUseCaseImpl(userGateway);
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(UserGateway userGateway, PasswordHasher passwordHasher) {
        return new ChangePasswordUseCaseImpl(userGateway, passwordHasher);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserGateway userGateway) {
        return new DeleteUserUseCaseImpl(userGateway);
    }

    @Bean
    public UserModerationUseCase userModerationUseCase(UserGateway userGateway) {
        return new UserModerationUseCaseImpl(userGateway);
    }

    // Question Domain
    @Bean
    public QuestionFactory questionFactory() {
        return new DefaultQuestionFactory();
    }

    @Bean
    public SubmitQuestionUseCase submitQuestionUseCase(QuestionGateway questionGateway, QuestionFactory factory) {
        return new SubmitQuestionUseCaseImpl(questionGateway, factory);
    }

    @Bean
    public EvaluateVotingResultUseCase evaluateVotingResultUseCase(QuestionGateway questionGateway) {
        return new MockEvaluateVotingResultUseCaseImpl(questionGateway);
    }

    @Bean
    public UpdateQuestionUseCase updateQuestionUseCase(QuestionGateway questionGateway, UserGateway userGateway) {
        return new UpdateQuestionUseCaseImpl(questionGateway, userGateway);
    }

    @Bean
    public ListQuestionsUseCase listQuestionsUseCase(QuestionGateway questionGateway) {
        return new ListQuestionsUseCaseImpl(questionGateway);
    }

    @Bean
    public GetQuestionByIdUseCase getQuestionByIdUseCase(QuestionGateway questionGateway) {
        return new GetQuestionByIdUseCaseImpl(questionGateway);
    }

    @Bean
    public ModerateQuestionUseCase moderateQuestionUseCase(QuestionGateway questionGateway) {
        return new ModerateQuestionUseCaseImpl(questionGateway);
    }

    @Bean
    public GetQuestionVotingDetailsUseCase getQuestionVotingDetailsUseCase(QuestionGateway questionGateway,
                                                                         VoteGateway voteGateway,
                                                                         FeedbackGateway feedbackGateway,
                                                                         UserGateway userGateway) {
        return new GetQuestionVotingDetailsUseCaseImpl(questionGateway, voteGateway, feedbackGateway, userGateway);
    }

    // Metrics Domain - Feedback
    @Bean
    public FeedbackGateway feedbackGateway(FeedbackQuestionJpaRepository feedbackQuestionJpaRepository,
                                           FeedbackQuestionMapper feedbackQuestionMapper,
                                           QuestionMapper questionMapper) {
        return new FeedbackGatewayImpl(feedbackQuestionJpaRepository, feedbackQuestionMapper, questionMapper);
    }

    @Bean
    public SubmitFeedbackOnQuestionUseCase submitFeedbackOnQuestionUseCase(FeedbackGateway feedbackGateway,
                                                                           UserGateway userGateway,
                                                                           QuestionGateway questionGateway) {
        return new SubmitFeedbackOnQuestionUseCaseImpl(feedbackGateway, userGateway, questionGateway);
    }

    @Bean
    public ListFeedbacksForQuestionUseCase listFeedbacksForQuestionUseCase(FeedbackGateway feedbackGateway,
                                                                           QuestionGateway questionGateway) {
        return new ListFeedbacksForQuestionUseCaseImpl(feedbackGateway, questionGateway);
    }

    // Metrics Domain - Vote
    @Bean
    public VoteGateway voteGateway(VoteJpaRepository voteJpaRepository, VoteMapper voteMapper) {
        return new VoteGatewayImpl(voteJpaRepository, voteMapper);
    }

    @Bean
    public CastVoteUseCase castVoteUseCase(UserGateway userGateway, QuestionGateway questionGateway, VoteGateway voteGateway) {
        return new CastVoteUseCaseImpl(userGateway, questionGateway, voteGateway);
    }

    @Bean
    public ListVotesForQuestionUseCase listVotesForQuestionUseCase(VoteGateway voteGateway, QuestionGateway questionGateway) {
        return new ListVotesForQuestionUseCaseImpl(voteGateway, questionGateway);
    }

    @Bean
    public ReactToFeedbackUseCase reactToFeedbackUseCase(FeedbackGateway feedbackGateway, UserGateway userGateway) {
        return new ReactToFeedbackUseCaseImpl(feedbackGateway, userGateway);
    }

    @Bean
    public UpdateFeedbackCommentUseCase updateFeedbackCommentUseCase(FeedbackGateway feedbackGateway) {
        return new UpdateFeedbackCommentUseCaseImpl(feedbackGateway);
    }
}