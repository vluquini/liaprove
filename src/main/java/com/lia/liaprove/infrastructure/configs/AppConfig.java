package com.lia.liaprove.infrastructure.configs;

import com.lia.liaprove.application.gateways.algorithms.bayesian.BayesianGateway;
import com.lia.liaprove.application.gateways.algorithms.genetic.GeneticGateway;
import com.lia.liaprove.application.gateways.algorithms.genetic.VoteMultiplierGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.assessment.CertificateGateway;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.JobDescriptionAnalysisGateway;
import com.lia.liaprove.application.gateways.metrics.AssessmentAttemptVoteGateway;
import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.algorithms.bayesian.BayesianNetworkUseCaseImpl;
import com.lia.liaprove.application.services.algorithms.genetic.DefaultFitnessEvaluatorImpl;
import com.lia.liaprove.application.services.algorithms.genetic.GeneticAlgorithmUseCaseImpl;
import com.lia.liaprove.application.services.algorithms.genetic.ManageVoteWeightUseCaseImpl;
import com.lia.liaprove.application.services.assessment.*;
import com.lia.liaprove.application.services.metrics.*;
import com.lia.liaprove.application.services.question.*;
import com.lia.liaprove.application.services.user.*;
import com.lia.liaprove.core.algorithms.bayesian.BayesianConfig;
import com.lia.liaprove.core.algorithms.genetic.FitnessEvaluator;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;
import com.lia.liaprove.core.usecases.algorithms.genetic.GeneticAlgorithmUseCase;
import com.lia.liaprove.core.usecases.assessments.*;
import com.lia.liaprove.core.usecases.metrics.*;
import com.lia.liaprove.core.usecases.question.*;
import com.lia.liaprove.core.usecases.algorithms.genetic.ManageVoteWeightUseCase;
import com.lia.liaprove.core.usecases.user.*;
import com.lia.liaprove.infrastructure.mappers.metrics.AssessmentAttemptVoteMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackAssessmentMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.VoteMapper;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptVoteJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackAssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.VoteJpaRepository;
import com.lia.liaprove.infrastructure.services.metrics.AssessmentAttemptVoteGatewayImpl;
import com.lia.liaprove.infrastructure.services.metrics.FeedbackGatewayImpl;
import com.lia.liaprove.infrastructure.services.user.PasswordHasherImpl;
import com.lia.liaprove.infrastructure.services.user.UserGatewayImpl;
import com.lia.liaprove.infrastructure.services.metrics.VoteGatewayImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

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
    public PreAnalyzeQuestionUseCase preAnalyzeQuestionUseCase(QuestionPreAnalysisGateway questionPreAnalysisGateway) {
        return new PreAnalyzeQuestionUseCaseImpl(questionPreAnalysisGateway);
    }

    @Bean
    public PrepareQuestionSubmissionUseCase prepareQuestionSubmissionUseCase(QuestionPreAnalysisGateway questionPreAnalysisGateway) {
        return new PrepareQuestionSubmissionUseCaseImpl(questionPreAnalysisGateway);
    }

    @Bean
    public AnalyzeJobDescriptionUseCase analyzeJobDescriptionUseCase(JobDescriptionAnalysisGateway jobDescriptionAnalysisGateway) {
        return new AnalyzeJobDescriptionUseCaseImpl(jobDescriptionAnalysisGateway);
    }

    @Bean
    public GenerateAttemptPreAnalysisUseCase generateAttemptPreAnalysisUseCase(
            AssessmentAttemptGateway assessmentAttemptGateway,
            UserGateway userGateway,
            AttemptPreAnalysisGateway attemptPreAnalysisGateway) {
        return new GenerateAttemptPreAnalysisUseCaseImpl(
                assessmentAttemptGateway,
                userGateway,
                attemptPreAnalysisGateway
        );
    }

    @Bean
    public CreateRecruiterOpenQuestionUseCase createRecruiterOpenQuestionUseCase(QuestionGateway questionGateway,
                                                                                 QuestionFactory questionFactory) {
        return new CreateRecruiterOpenQuestionUseCaseImpl(questionGateway, questionFactory);
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
                                           FeedbackAssessmentJpaRepository feedbackAssessmentJpaRepository, // Added FeedbackAssessmentJpaRepository
                                           FeedbackQuestionMapper feedbackQuestionMapper,
                                           FeedbackAssessmentMapper feedbackAssessmentMapper, // Added FeedbackAssessmentMapper
                                           QuestionMapper questionMapper) {
        // Updated constructor to include new dependencies
        return new FeedbackGatewayImpl(feedbackQuestionJpaRepository, feedbackAssessmentJpaRepository, feedbackQuestionMapper, feedbackAssessmentMapper, questionMapper);
    }

    @Bean
    public SubmitFeedbackOnQuestionUseCase submitFeedbackOnQuestionUseCase(FeedbackGateway feedbackGateway,
                                                                           UserGateway userGateway,
                                                                           QuestionGateway questionGateway) {
        return new SubmitFeedbackOnQuestionUseCaseImpl(feedbackGateway, userGateway, questionGateway);
    }

    @Bean
    public SubmitFeedbackOnAssessmentUseCase submitFeedbackOnAssessmentUseCase(
            FeedbackGateway feedbackGateway,
            UserGateway userGateway,
            AssessmentAttemptGateway assessmentAttemptGateway) {
        return new SubmitFeedbackOnAssessmentUseCaseImpl(feedbackGateway, userGateway, assessmentAttemptGateway);
    }

    @Bean
    public AssessmentAttemptVoteGateway assessmentAttemptVoteGateway(
            AssessmentAttemptVoteJpaRepository assessmentAttemptVoteJpaRepository,
            AssessmentAttemptVoteMapper assessmentAttemptVoteMapper) {
        return new AssessmentAttemptVoteGatewayImpl(assessmentAttemptVoteJpaRepository, assessmentAttemptVoteMapper);
    }

    @Bean
    public CastVoteOnAssessmentAttemptUseCase castVoteOnAssessmentAttemptUseCase(
            UserGateway userGateway,
            AssessmentAttemptGateway assessmentAttemptGateway,
            AssessmentAttemptVoteGateway assessmentAttemptVoteGateway) {
        return new CastVoteOnAssessmentAttemptUseCaseImpl(userGateway, assessmentAttemptGateway, assessmentAttemptVoteGateway);
    }

    @Bean
    public ListPublicMiniProjectAttemptsUseCase listPublicMiniProjectAttemptsUseCase(
            AssessmentAttemptGateway assessmentAttemptGateway) {
        return new ListPublicMiniProjectAttemptsUseCaseImpl(assessmentAttemptGateway);
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

    // Assessment Domain
    @Bean
    public GenerateSystemAssessmentUseCase generateSystemAssessmentUseCase(QuestionGateway questionGateway) {
        return new GenerateSystemAssessmentUseCaseImpl(questionGateway);
    }

    @Bean
    public CreatePersonalizedAssessmentUseCase createPersonalizedAssessmentUseCase(AssessmentGateway assessmentGateway,
                                                                                   QuestionGateway questionGateway,
                                                                                   UserGateway userGateway) {
        return new CreatePersonalizedAssessmentUseCaseImpl(assessmentGateway, questionGateway, userGateway);
    }

    @Bean
    public DeletePersonalizedAssessmentUseCase deletePersonalizedAssessmentUseCase(AssessmentGateway assessmentGateway,
                                                                                   AssessmentAttemptGateway assessmentAttemptGateway,
                                                                                   UserGateway userGateway) {
        return new DeletePersonalizedAssessmentUseCaseImpl(assessmentGateway, assessmentAttemptGateway, userGateway);
    }

    @Bean
    public BayesianConfig bayesianConfig() {
        // Configuração padrão para o MVP
        return BayesianConfig.defaults();
    }

    @Bean
    public GeneticConfig geneticConfig() {
        return GeneticConfig.defaults();
    }

    @Bean
    public FitnessEvaluator fitnessEvaluator(GeneticConfig geneticConfig) {
        return DefaultFitnessEvaluatorImpl.defaultEvaluator(geneticConfig);
    }

    @Bean
    public GeneticAlgorithmUseCase geneticAlgorithmUseCase(GeneticConfig geneticConfig,
                                                           FitnessEvaluator fitnessEvaluator,
                                                           GeneticGateway geneticGateway) {
        return new GeneticAlgorithmUseCaseImpl(geneticConfig, fitnessEvaluator, geneticGateway, new Random());
    }

    @Bean
    public ManageVoteWeightUseCase manageVoteWeightUseCase(GeneticAlgorithmUseCase geneticAlgorithmUseCase,
                                                           UserGateway userGateway,
                                                           VoteMultiplierGateway voteMultiplierGateway,
                                                           GeneticConfig geneticConfig) {
        return new ManageVoteWeightUseCaseImpl(geneticAlgorithmUseCase, userGateway, voteMultiplierGateway, geneticConfig);
    }

    @Bean
    public BayesianNetworkUseCase bayesianNetworkUseCase(BayesianGateway bayesianGateway, BayesianConfig bayesianConfig) {
        return new BayesianNetworkUseCaseImpl(bayesianGateway, bayesianConfig);
    }

    @Bean
    public SuggestQuestionsForAssessmentUseCase suggestQuestionsForAssessmentUseCase(BayesianNetworkUseCase bayesianNetworkUseCase,
                                                                                     UserGateway userGateway) {
        return new SuggestQuestionsForAssessmentUseCaseImpl(bayesianNetworkUseCase, userGateway);
    }

    @Bean
    public StartNewAssessmentUseCase startNewAssessmentUseCase(AssessmentGateway assessmentGateway,
                                                               AssessmentAttemptGateway assessmentAttemptGateway,
                                                               UserGateway userGateway,
                                                               GenerateSystemAssessmentUseCase generateSystemAssessmentUseCase) {
        return new StartNewAssessmentUseCaseImpl(assessmentGateway, assessmentAttemptGateway, userGateway, generateSystemAssessmentUseCase);
    }

    @Bean
    public IssueCertificateUseCase issueCertificateUseCase(CertificateGateway certificateGateway) {
        return new IssueCertificateUseCaseImpl(certificateGateway, "https://liaprove.com/certificates");
    }

    @Bean
    public GetCertificateByNumberUseCase getCertificateByNumberUseCase(CertificateGateway certificateGateway) {
        return new GetCertificateByNumberUseCaseImpl(certificateGateway);
    }

    @Bean
    public SubmitAssessmentUseCase submitAssessmentUseCase(AssessmentAttemptGateway assessmentAttemptGateway,
                                                           IssueCertificateUseCase issueCertificateUseCase) {
        return new SubmitAssessmentUseCaseImpl(assessmentAttemptGateway, issueCertificateUseCase);
    }

    @Bean
    public EvaluateAssessmentAttemptUseCase finalizeAssessmentAttemptUseCase(AssessmentAttemptGateway assessmentAttemptGateway,
                                                                             UserGateway userGateway) {
        return new EvaluateAssessmentAttemptUseCaseImpl(assessmentAttemptGateway, userGateway);
    }

    @Bean
    public EvaluateCommunityReviewAssessmentAttemptUseCase evaluateCommunityReviewAssessmentAttemptUseCase(
            AssessmentAttemptGateway assessmentAttemptGateway) {
        return new MockEvaluateCommunityReviewAssessmentAttemptUseCaseImpl(assessmentAttemptGateway);
    }

    @Bean
    public ListAllAssessmentAttemptsUseCase listAllAssessmentAttemptsUseCase(UserGateway userGateway,
                                                                             AssessmentAttemptGateway assessmentAttemptGateway) {
        return new ListAllAssessmentAttemptsUseCaseImpl(userGateway, assessmentAttemptGateway);
    }

    @Bean
    public ListAttemptsForMyAssessmentUseCase listAttemptsForMyAssessmentUseCase(AssessmentGateway assessmentGateway,
                                                                                 AssessmentAttemptGateway assessmentAttemptGateway) {
        return new ListAttemptsForMyAssessmentUseCaseImpl(assessmentGateway, assessmentAttemptGateway);
    }

    @Bean
    public UpdatePersonalizedAssessmentUseCase updatePersonalizedAssessmentUseCase(AssessmentGateway assessmentGateway,
                                                                                   UserGateway userGateway) {
        return new UpdatePersonalizedAssessmentUseCaseImpl(assessmentGateway, userGateway);
    }

    @Bean
    public UpdateExpiredAssessmentsStatusUseCase updateExpiredAssessmentsStatusUseCase(AssessmentGateway assessmentGateway) {
        return new UpdateExpiredAssessmentsStatusUseCaseImpl(assessmentGateway);
    }

    @Bean
    public GetAssessmentAttemptDetailsUseCase getAssessmentAttemptDetailsUseCase(AssessmentAttemptGateway assessmentAttemptGateway,
                                                                                 UserGateway userGateway) {
        return new GetAssessmentAttemptDetailsUseCaseImpl(assessmentAttemptGateway, userGateway);
    }
}
