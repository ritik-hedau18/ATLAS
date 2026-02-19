import React, { useState, useEffect } from 'react';
import { 
  Briefcase, 
  Users, 
  MessageSquare, 
  Bell, 
  Search, 
  User as UserIcon, 
  ThumbsUp, 
  Share2, 
  Plus, 
  Trash2, 
  CheckCircle, 
  ShieldAlert, 
  Award, 
  MapPin, 
  Globe, 
  TrendingUp, 
  Send, 
  Bookmark, 
  Heart,
  Network
} from 'lucide-react';

// Interfaces
interface Skill {
  id: string;
  name: string;
  endorsements: number;
}

interface Experience {
  id: string;
  company: string;
  title: string;
  duration: string;
  description: string;
}

interface Education {
  id: string;
  institution: string;
  degree: string;
  field: string;
  years: string;
}

interface Post {
  id: string;
  authorName: string;
  authorHeadline: string;
  authorPhoto: string;
  content: string;
  likes: number;
  commentsCount: number;
  shares: number;
  timestamp: string;
  liked: boolean;
  comments: { author: string; content: string }[];
}

interface Job {
  id: string;
  title: string;
  company: string;
  logo: string;
  location: string;
  type: string;
  salary: string;
  skillsRequired: string[];
  saved: boolean;
  applied: boolean;
}

interface NotificationItem {
  id: string;
  type: string;
  actor: string;
  message: string;
  time: string;
  read: boolean;
}

export default function App() {
  // Theme & Tab State
  const [theme, setTheme] = useState<'dark' | 'light'>('dark');
  const [activeTab, setActiveTab] = useState<'feed' | 'profile' | 'network' | 'jobs' | 'notifications' | 'search'>('feed');
  
  // Auth State
  const [isLoggedIn, setIsLoggedIn] = useState(true);
  const [authEmail, setAuthEmail] = useState('');
  const [authPassword, setAuthPassword] = useState('');
  const [authName, setAuthName] = useState('');
  const [isRegisterMode, setIsRegisterMode] = useState(false);

  // Search State
  const [searchQuery, setSearchQuery] = useState('');
  const [searchSuggestions, setSearchSuggestions] = useState<string[]>([]);
  const [searchResults, setSearchResults] = useState<{ users: any[], posts: Post[], jobs: Job[] }>({ users: [], posts: [], jobs: [] });

  // Profile State
  const [profileName, setProfileName] = useState('Dr. Ritik Sharma');
  const [profileHeadline, setProfileHeadline] = useState('Senior Backend & Cloud Architect | Java Microservices & Distributed Systems Expert');
  const [profileBio, setProfileBio] = useState('Passionate about crafting production-ready distributed backends, streaming architectures using Kafka, and scale graph processing with Neo4j. Over 7 years of engineering experience across financial services and developer platforms.');
  const [profileLocation, setProfileLocation] = useState('Bengaluru, Karnataka, India');
  const [profileWebsite, setProfileWebsite] = useState('ritiksharma.dev');
  const [skills, setSkills] = useState<Skill[]>([
    { id: '1', name: 'Java Spring Boot & Cloud', endorsements: 42 },
    { id: '2', name: 'Kafka & Event Streaming', endorsements: 38 },
    { id: '3', name: 'Neo4j Graph Database', endorsements: 29 },
    { id: '4', name: 'Redis Cache & Clustering', endorsements: 31 },
    { id: '5', name: 'Kubernetes & Docker Engine', endorsements: 35 },
    { id: '6', name: 'Elasticsearch Search & Analytics', endorsements: 24 }
  ]);
  const [experiences, setExperiences] = useState<Experience[]>([
    { id: '1', company: 'Google Cloud Platform', title: 'Senior Cloud Engineer', duration: '2024 - Present', description: 'Architecting highly scalable Java-based core infrastructure APIs and optimizing container performance in large-scale Kubernetes environments.' },
    { id: '2', company: 'VAULT Financial Systems', title: 'Lead Backend Engineer', duration: '2022 - 2024', description: 'Led the development of highly secured real-time payment settlement gateways utilizing Spring Security, PostgreSQL partitioning, and Kafka streams.' }
  ]);
  const [educations, setEducations] = useState<Education[]>([
    { id: '1', institution: 'Indian Institute of Science (IISc)', degree: 'Master of Technology (M.Tech)', field: 'Computer Science & Engineering', years: '2020 - 2022' }
  ]);
  
  // Post Creator State
  const [newPostContent, setNewPostContent] = useState('');
  const [postModerationError, setPostModerationError] = useState<string | null>(null);

  // Network State
  const [selectedNetworkUser, setSelectedNetworkUser] = useState<string | null>(null);
  const [shortestPathStart, setShortestPathStart] = useState('Ritik Sharma');
  const [shortestPathEnd, setShortestPathEnd] = useState('Elon Musk');
  const [computedShortestPath, setComputedShortestPath] = useState<string[]>([]);

  // Job Search State
  const [jobSearchQuery, setJobSearchQuery] = useState('');
  const [jobLocationQuery, setJobLocationQuery] = useState('');

  // Sample Data Sets (to emulate microservice database logs)
  const [posts, setPosts] = useState<Post[]>([
    {
      id: 'post-1',
      authorName: 'Sarah Jenkins',
      authorHeadline: 'Principal AI Researcher at Hugging Face',
      authorPhoto: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150',
      content: 'Thrilled to launch our new rest endpoints for content moderation pipelines! We deployed a lightweight BERT model that classifies toxicity under 15ms. Try typing hate or spam words in the creator input above to see the real-time AI reject flow in action! 🤖🔥',
      likes: 124,
      commentsCount: 3,
      shares: 12,
      timestamp: '2 hours ago',
      liked: false,
      comments: [
        { author: 'David Chen', content: 'Incredible speed! Is the latency optimized via cache pooling?' },
        { author: 'Ritik Sharma', content: 'Fantastic launch Sarah, will integrate this into our gateway filters.' }
      ]
    },
    {
      id: 'post-2',
      authorName: 'Alex Mercer',
      authorHeadline: 'Staff Site Reliability Engineer at Netflix',
      authorPhoto: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150',
      content: 'Scaling Redis Cluster to handle 5 million reads/sec for our feed delivery system. Fan-out on write is fantastic, but caching active user feed indexes in sorted sets is what really keeps the read path under 5ms! 🚀⚡',
      likes: 310,
      commentsCount: 1,
      shares: 45,
      timestamp: '5 hours ago',
      liked: true,
      comments: [
        { author: 'Lisa Ray', content: 'What is the TTL policy on inactive user feeds?' }
      ]
    }
  ]);

  const [jobs, setJobs] = useState<Job[]>([
    {
      id: 'job-1',
      title: 'Senior Java Backend Engineer (Spring Cloud)',
      company: 'Stripe Payments',
      logo: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150',
      location: 'Bengaluru, India (Hybrid)',
      type: 'Full-Time',
      salary: '₹35L - ₹50L',
      skillsRequired: ['Java Spring Boot & Cloud', 'Kafka & Event Streaming', 'Redis Cache & Clustering'],
      saved: false,
      applied: false
    },
    {
      id: 'job-2',
      title: 'Distributed Systems & Network Architect',
      company: 'Amazon Web Services',
      logo: 'https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=150',
      location: 'Seattle, USA (Remote)',
      type: 'Full-Time',
      salary: '$180,000 - $220,000',
      skillsRequired: ['Kubernetes & Docker Engine', 'Kafka & Event Streaming', 'Elasticsearch Search & Analytics'],
      saved: false,
      applied: false
    },
    {
      id: 'job-3',
      title: 'Machine Learning Infrastructure Engineer',
      company: 'Meta AI',
      logo: 'https://images.unsplash.com/photo-1614741118887-7a4ee193a5fa?w=150',
      location: 'Menlo Park, CA',
      type: 'Full-Time',
      salary: '$200,000 - $250,000',
      skillsRequired: ['Python', 'Kubernetes & Docker Engine', 'Redis Cache & Clustering'],
      saved: true,
      applied: false
    }
  ]);

  const [notifications, setNotifications] = useState<NotificationItem[]>([
    { id: '1', type: 'CONNECTION_REQUEST', actor: 'Alice Cooper', message: 'sent you a connection request.', time: '10 min ago', read: false },
    { id: '2', type: 'POST_LIKE', actor: 'Alex Mercer', message: 'liked your post about distributed tracing.', time: '1 hour ago', read: false },
    { id: '3', type: 'PROFILE_VIEW', actor: 'Recruiter at Google', message: 'viewed your profile.', time: 'Yesterday', read: true }
  ]);

  // Autocomplete Suggestions Mock API
  useEffect(() => {
    if (searchQuery.trim().length > 1) {
      const allNames = ['Ritik Sharma', 'Sarah Jenkins', 'Alex Mercer', 'David Chen', 'Elon Musk', 'Sundar Pichai', 'Satya Nadella'];
      const filtered = allNames.filter(name => name.toLowerCase().includes(searchQuery.toLowerCase()));
      setSearchSuggestions(filtered);
    } else {
      setSearchSuggestions([]);
    }
  }, [searchQuery]);

  // Handle Search Execution
  const executeSearch = (queryStr: string) => {
    setSearchQuery(queryStr);
    setSearchSuggestions([]);
    
    // Simulate Elasticsearch query logic
    const matchedUsers = [
      { id: 'u-1', name: 'Ritik Sharma', headline: 'Senior Backend & Cloud Architect', location: 'Bengaluru' },
      { id: 'u-2', name: 'Sarah Jenkins', headline: 'Principal AI Researcher', location: 'Paris' }
    ].filter(u => u.name.toLowerCase().includes(queryStr.toLowerCase()) || u.headline.toLowerCase().includes(queryStr.toLowerCase()));

    const matchedPosts = posts.filter(p => p.content.toLowerCase().includes(queryStr.toLowerCase()));
    const matchedJobs = jobs.filter(j => j.title.toLowerCase().includes(queryStr.toLowerCase()) || j.company.toLowerCase().includes(queryStr.toLowerCase()));

    setSearchResults({ users: matchedUsers, posts: matchedPosts, jobs: matchedJobs });
    setActiveTab('search');
  };

  // Hugging Face rest endpoints moderation simulator
  const handleCreatePost = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPostContent.trim()) return;

    // Simulate AI Service REST endpoint call
    const contentLower = newPostContent.toLowerCase();
    const toxicWords = ['hate', 'kill', 'toxic', 'spam', 'scam', 'abuse'];
    const foundToxic = toxicWords.find(w => contentLower.includes(w));

    if (foundToxic) {
      setPostModerationError(`🚨 AI Content Moderation Blocked! Hugging Facerest-api flagged content for: HATE_SPEECH/SPAM (toxicity score: 0.94). Forbidden keyword: "${foundToxic}"`);
      return;
    }

    setPostModerationError(null);
    const newPost: Post = {
      id: 'post-' + Date.now(),
      authorName: profileName,
      authorHeadline: profileHeadline,
      authorPhoto: 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150',
      content: newPostContent,
      likes: 0,
      commentsCount: 0,
      shares: 0,
      timestamp: 'Just now',
      liked: false,
      comments: []
    };

    setPosts([newPost, ...posts]);
    setNewPostContent('');
    
    // Simulate Kafka Streams fan-out: add a simulated notification
    setTimeout(() => {
      const newNotif: NotificationItem = {
        id: 'notif-' + Date.now(),
        type: 'POST_LIKE',
        actor: 'David Chen',
        message: 'fanned out on write: feed successfully indexed to Redis Cluster for David Chen.',
        time: 'Just now',
        read: false
      };
      setNotifications([newNotif, ...notifications]);
    }, 1000);
  };

  // Skill Endorsements Simulator
  const handleEndorseSkill = (skillId: string) => {
    setSkills(skills.map(s => s.id === skillId ? { ...s, endorsements: s.endorsements + 1 } : s));
  };

  // Shortest Path Graph Computation (Neo4j client simulation)
  const computeShortestPath = () => {
    if (shortestPathStart === shortestPathEnd) {
      setComputedShortestPath([shortestPathStart]);
      return;
    }
    // Mocking standard path nodes for a small-world network
    const path = [
      shortestPathStart,
      'Sarah Jenkins (Principal AI Researcher)',
      'David Chen (Principal PM at SpaceX)',
      shortestPathEnd
    ];
    setComputedShortestPath(path);
  };

  // Job matching recommendation engine (cosine similarity mock)
  const getJobRecomScore = (jobSkills: string[]) => {
    const userSkillNames = skills.map(s => s.name.toLowerCase());
    const matched = jobSkills.filter(s => userSkillNames.includes(s.toLowerCase()));
    return Math.round((matched.length / jobSkills.length) * 100);
  };

  return (
    <div className={`min-h-screen ${theme === 'dark' ? 'bg-[#090d16] text-[#f3f4f6]' : 'bg-[#f4f5f7] text-[#1f2937]'}`}>
      
      {/* 🌐 Glassmorphism Header */}
      <header className="sticky top-0 z-50 backdrop-blur-md bg-opacity-70 border-b border-gray-800 bg-[#0c1322] px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="bg-blue-600 p-2 rounded-lg text-white font-bold text-xl flex items-center justify-center gap-1 shadow-md shadow-blue-500/20">
            <span>A</span>
          </div>
          <span className="font-extrabold text-2xl tracking-wider font-outfit text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-sky-400 to-indigo-400">
            ATLAS
          </span>
        </div>

        {/* Search bar */}
        <div className="relative w-96 hidden md:block">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-gray-400" />
          </div>
          <input
            type="text"
            placeholder="Search people, skills, jobs..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && executeSearch(searchQuery)}
            className="w-full pl-10 pr-4 py-2 bg-gray-900 bg-opacity-60 border border-gray-700 rounded-full focus:outline-none focus:border-blue-500 text-sm text-gray-200 transition-all"
          />
          {searchSuggestions.length > 0 && (
            <div className="absolute top-12 left-0 w-full bg-gray-900 border border-gray-700 rounded-xl shadow-2xl z-50 overflow-hidden">
              {searchSuggestions.map((s, idx) => (
                <div 
                  key={idx} 
                  onClick={() => executeSearch(s)}
                  className="px-4 py-2 hover:bg-gray-800 cursor-pointer flex items-center gap-2 text-sm text-gray-300 transition-colors"
                >
                  <Search className="h-4 w-4 text-gray-500" />
                  {s}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Action items */}
        <div className="flex items-center gap-6">
          <nav className="flex items-center gap-2 md:gap-4">
            <button 
              onClick={() => setActiveTab('feed')} 
              className={`p-2 rounded-xl flex items-center gap-2 text-sm transition-all duration-200 ${activeTab === 'feed' ? 'bg-blue-600/20 text-blue-400 border border-blue-600/30' : 'text-gray-400 hover:text-white'}`}
            >
              <MessageSquare className="h-5 w-5" />
              <span className="hidden lg:inline">Feed</span>
            </button>
            <button 
              onClick={() => setActiveTab('network')} 
              className={`p-2 rounded-xl flex items-center gap-2 text-sm transition-all duration-200 ${activeTab === 'network' ? 'bg-blue-600/20 text-blue-400 border border-blue-600/30' : 'text-gray-400 hover:text-white'}`}
            >
              <Network className="h-5 w-5" />
              <span className="hidden lg:inline">Network</span>
            </button>
            <button 
              onClick={() => setActiveTab('jobs')} 
              className={`p-2 rounded-xl flex items-center gap-2 text-sm transition-all duration-200 ${activeTab === 'jobs' ? 'bg-blue-600/20 text-blue-400 border border-blue-600/30' : 'text-gray-400 hover:text-white'}`}
            >
              <Briefcase className="h-5 w-5" />
              <span className="hidden lg:inline">Jobs</span>
            </button>
            <button 
              onClick={() => setActiveTab('notifications')} 
              className={`p-2 rounded-xl flex items-center gap-2 text-sm transition-all duration-200 relative ${activeTab === 'notifications' ? 'bg-blue-600/20 text-blue-400 border border-blue-600/30' : 'text-gray-400 hover:text-white'}`}
            >
              <Bell className="h-5 w-5" />
              {notifications.filter(n => !n.read).length > 0 && (
                <span className="absolute -top-1 -right-1 bg-red-500 text-white rounded-full text-xxs w-4 h-4 flex items-center justify-center font-bold">
                  {notifications.filter(n => !n.read).length}
                </span>
              )}
              <span className="hidden lg:inline">Alerts</span>
            </button>
            <button 
              onClick={() => setActiveTab('profile')} 
              className={`p-2 rounded-xl flex items-center gap-2 text-sm transition-all duration-200 ${activeTab === 'profile' ? 'bg-blue-600/20 text-blue-400 border border-blue-600/30' : 'text-gray-400 hover:text-white'}`}
            >
              <UserIcon className="h-5 w-5" />
              <span className="hidden lg:inline">Profile</span>
            </button>
          </nav>
        </div>
      </header>

      {/* 🚀 Main App Section */}
      <main className="max-w-7xl mx-auto px-6 py-8 grid grid-cols-1 lg:grid-cols-4 gap-8">
        
        {/* Left Sidebar (User details) */}
        <aside className="lg:col-span-1 space-y-6">
          <div className="bg-[#0c1322] border border-gray-800 rounded-3xl overflow-hidden shadow-xl shadow-black/10">
            <div className="h-24 bg-gradient-to-r from-blue-600 to-indigo-600 relative"></div>
            <div className="p-6 relative text-center">
              <div className="w-20 h-20 rounded-full border-4 border-[#0c1322] bg-gray-700 overflow-hidden mx-auto -mt-16 mb-4 shadow-lg">
                <img 
                  src="https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150" 
                  alt="Avatar"
                  className="w-full h-full object-cover"
                />
              </div>
              <h2 className="font-bold text-xl text-white font-outfit">{profileName}</h2>
              <p className="text-gray-400 text-xs mt-1 leading-relaxed line-clamp-2">{profileHeadline}</p>
              
              <div className="border-t border-gray-800 my-6"></div>
              
              <div className="text-left space-y-3">
                <div className="flex items-center justify-between text-xs">
                  <span className="text-gray-400">Profile views</span>
                  <span className="text-blue-400 font-semibold">1,024</span>
                </div>
                <div className="flex items-center justify-between text-xs">
                  <span className="text-gray-400">Graph Connections</span>
                  <span className="text-blue-400 font-semibold">412 (Neo4j)</span>
                </div>
                <div className="flex items-center justify-between text-xs">
                  <span className="text-gray-400">Score completeness</span>
                  <span className="text-emerald-400 font-semibold">95%</span>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl">
            <h3 className="font-bold text-sm text-gray-300 uppercase tracking-wider mb-4 flex items-center gap-2">
              <TrendingUp className="h-4 w-4 text-blue-500" />
              Cluster Statistics
            </h3>
            <div className="space-y-4 text-xs">
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Feed Latency (Redis)</span>
                <span className="text-emerald-400 font-mono">1.8ms</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Elasticsearch Index</span>
                <span className="text-emerald-400 font-mono">92ms response</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Kafka streams state</span>
                <span className="text-blue-400 font-mono">Healthy (3 nodes)</span>
              </div>
            </div>
          </div>
        </aside>

        {/* Center Panel (Dynamics Tab) */}
        <section className="lg:col-span-3 space-y-8">
          
          {/* Tab 1: Feed */}
          {activeTab === 'feed' && (
            <div className="space-y-6">
              
              {/* Post Creator Card */}
              <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl">
                <h3 className="font-bold text-lg mb-4 text-white font-outfit">Share an update</h3>
                <form onSubmit={handleCreatePost} className="space-y-4">
                  <textarea
                    rows={3}
                    placeholder="What is on your mind? (Note: typing words like 'hate', 'spam', 'kill' simulates toxicity blocking!)"
                    value={newPostContent}
                    onChange={(e) => setNewPostContent(e.target.value)}
                    className="w-full p-4 bg-gray-900 border border-gray-700 rounded-2xl focus:outline-none focus:border-blue-500 text-sm resize-none text-gray-200 transition-colors"
                  />
                  
                  {postModerationError && (
                    <div className="flex items-start gap-3 bg-red-950/40 border border-red-800 text-red-300 p-4 rounded-2xl text-xs">
                      <ShieldAlert className="h-5 w-5 text-red-500 shrink-0 mt-0.5" />
                      <span>{postModerationError}</span>
                    </div>
                  )}

                  <div className="flex justify-between items-center">
                    <span className="text-xs text-gray-400">
                      Visibility: <strong className="text-blue-400">PUBLIC</strong>
                    </span>
                    <button
                      type="submit"
                      className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-full text-sm font-semibold flex items-center gap-2 shadow-lg shadow-blue-500/25 transition-all"
                    >
                      <Send className="h-4 w-4" />
                      Post Created
                    </button>
                  </div>
                </form>
              </div>

              {/* Feed List */}
              <div className="space-y-6">
                {posts.map((post) => (
                  <div key={post.id} className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <img src={post.authorPhoto} alt="Author" className="w-12 h-12 rounded-full object-cover" />
                        <div>
                          <h4 className="font-bold text-white text-base">{post.authorName}</h4>
                          <p className="text-gray-400 text-xs">{post.authorHeadline}</p>
                        </div>
                      </div>
                      <span className="text-gray-500 text-xs">{post.timestamp}</span>
                    </div>

                    <p className="text-gray-200 text-sm leading-relaxed whitespace-pre-wrap">{post.content}</p>

                    <div className="flex items-center gap-6 border-t border-b border-gray-800 py-3 text-sm text-gray-400">
                      <button 
                        onClick={() => {
                          setPosts(posts.map(p => p.id === post.id ? { 
                            ...p, 
                            likes: p.liked ? p.likes - 1 : p.likes + 1, 
                            liked: !p.liked 
                          } : p));
                        }}
                        className={`flex items-center gap-2 hover:text-blue-400 transition-colors ${post.liked ? 'text-blue-400' : ''}`}
                      >
                        <Heart className={`h-5 w-5 ${post.liked ? 'fill-blue-500' : ''}`} />
                        <span>{post.likes} Likes</span>
                      </button>
                      <div className="flex items-center gap-2">
                        <MessageSquare className="h-5 w-5" />
                        <span>{post.commentsCount} Comments</span>
                      </div>
                      <button className="flex items-center gap-2 hover:text-blue-400 transition-colors">
                        <Share2 className="h-5 w-5" />
                        <span>{post.shares} Shares</span>
                      </button>
                    </div>

                    {/* Comments section */}
                    <div className="space-y-3">
                      {post.comments.map((comment, index) => (
                        <div key={index} className="bg-gray-900 bg-opacity-40 p-4 rounded-2xl text-xs space-y-1">
                          <span className="font-semibold text-blue-400">{comment.author}</span>
                          <p className="text-gray-300">{comment.content}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tab 2: Profile */}
          {activeTab === 'profile' && (
            <div className="space-y-6">
              
              {/* Profile Bio Card */}
              <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-6">
                <div className="flex justify-between items-start">
                  <div>
                    <h2 className="font-extrabold text-2xl text-white font-outfit">{profileName}</h2>
                    <p className="text-blue-400 text-sm mt-1">{profileHeadline}</p>
                  </div>
                </div>
                
                <div className="flex flex-wrap gap-4 text-xs text-gray-400">
                  <span className="flex items-center gap-1.5"><MapPin className="h-4 w-4" /> {profileLocation}</span>
                  <span className="flex items-center gap-1.5"><Globe className="h-4 w-4" /> {profileWebsite}</span>
                </div>

                <div className="space-y-2">
                  <h4 className="font-bold text-gray-300 uppercase tracking-wider text-xs">About</h4>
                  <p className="text-gray-300 text-sm leading-relaxed">{profileBio}</p>
                </div>
              </div>

              {/* Skillset Endorsements */}
              <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl">
                <h3 className="font-bold text-lg text-white font-outfit mb-4">Skills & Endorsements</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {skills.map((skill) => (
                    <div key={skill.id} className="p-4 bg-gray-900 bg-opacity-40 border border-gray-800 rounded-2xl flex justify-between items-center">
                      <div>
                        <h4 className="font-bold text-white text-sm">{skill.name}</h4>
                        <span className="text-gray-400 text-xs">{skill.endorsements} endorsements</span>
                      </div>
                      <button 
                        onClick={() => handleEndorseSkill(skill.id)}
                        className="px-3 py-1.5 bg-blue-600/10 hover:bg-blue-600/20 text-blue-400 rounded-lg text-xs font-semibold border border-blue-500/20 transition-all"
                      >
                        + Endorse
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              {/* Experiences & Education */}
              <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-6">
                <h3 className="font-bold text-lg text-white font-outfit">Experience</h3>
                <div className="space-y-6">
                  {experiences.map((exp) => (
                    <div key={exp.id} className="flex gap-4 items-start">
                      <div className="bg-blue-600/10 p-3 rounded-2xl text-blue-400">
                        <Briefcase className="h-6 w-6" />
                      </div>
                      <div>
                        <h4 className="font-bold text-white text-base">{exp.title}</h4>
                        <p className="text-gray-400 text-sm">{exp.company} • {exp.duration}</p>
                        <p className="text-gray-300 text-xs mt-2 leading-relaxed">{exp.description}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* Tab 3: Interactive Network Graph */}
          {activeTab === 'network' && (
            <div className="space-y-6">
              
              {/* Shortest Path Finder */}
              <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-6">
                <h3 className="font-bold text-lg text-white font-outfit">Neo4j Shortest Path Calculator</h3>
                <p className="text-gray-400 text-xs">Simulate a cypher graph query to find the degrees of connection between users on ATLAS.</p>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="text-xs text-gray-400 block mb-1">Start Node (User)</label>
                    <input 
                      type="text" 
                      value={shortestPathStart}
                      onChange={(e) => setShortestPathStart(e.target.value)}
                      className="w-full p-3 bg-gray-900 border border-gray-700 rounded-xl focus:outline-none text-xs text-gray-200"
                    />
                  </div>
                  <div>
                    <label className="text-xs text-gray-400 block mb-1">Destination Node (User)</label>
                    <input 
                      type="text" 
                      value={shortestPathEnd}
                      onChange={(e) => setShortestPathEnd(e.target.value)}
                      className="w-full p-3 bg-gray-900 border border-gray-700 rounded-xl focus:outline-none text-xs text-gray-200"
                    />
                  </div>
                </div>

                <button 
                  onClick={computeShortestPath}
                  className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-full text-xs font-semibold shadow-lg shadow-blue-500/25 transition-all"
                >
                  Run shortestPath() Cypher
                </button>

                {computedShortestPath.length > 0 && (
                  <div className="bg-gray-900 bg-opacity-40 p-6 rounded-2xl border border-gray-800">
                    <h4 className="font-bold text-xs text-gray-400 mb-4">COMPUTED PATHWAY:</h4>
                    <div className="flex flex-wrap items-center gap-3 text-xs">
                      {computedShortestPath.map((node, index) => (
                        <React.Fragment key={index}>
                          {index > 0 && <span className="text-blue-500 font-bold">&rarr;</span>}
                          <div className="bg-blue-600/10 border border-blue-500/30 text-blue-400 px-3 py-1.5 rounded-xl font-medium">
                            {node}
                          </div>
                        </React.Fragment>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              {/* Neo4j Visual Graph */}
              <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-6">
                <h3 className="font-bold text-lg text-white font-outfit">Visual Connections (1st & 2nd Degrees)</h3>
                
                <div className="h-80 w-full bg-gray-950 border border-gray-800 rounded-2xl relative overflow-hidden flex items-center justify-center">
                  <div className="absolute inset-0 flex items-center justify-center opacity-10">
                    {/* SVG background grid */}
                    <div className="w-full h-full bg-[radial-gradient(#3b82f6_1px,transparent_1px)] [background-size:16px_16px]"></div>
                  </div>

                  {/* Interactive SVG graph rendering */}
                  <svg className="w-full h-full" viewBox="0 0 600 300">
                    {/* Connection Lines */}
                    <line x1="300" y1="150" x2="150" y2="80" stroke="#3b82f6" strokeWidth="2" strokeDasharray="4" />
                    <line x1="300" y1="150" x2="450" y2="80" stroke="#3b82f6" strokeWidth="2" />
                    <line x1="300" y1="150" x2="300" y2="250" stroke="#3b82f6" strokeWidth="2" />
                    <line x1="150" y1="80" x2="70" y2="150" stroke="#4b5563" strokeWidth="1" />
                    <line x1="450" y1="80" x2="530" y2="150" stroke="#4b5563" strokeWidth="1" />

                    {/* Central Node (User) */}
                    <circle cx="300" cy="150" r="16" fill="#2563eb" className="cursor-pointer" onClick={() => setSelectedNetworkUser('Ritik Sharma (You)')} />
                    <text x="300" y="180" textAnchor="middle" fill="#ffffff" fontSize="10" fontWeight="bold">You</text>

                    {/* 1st Degree Nodes */}
                    <circle cx="150" cy="80" r="12" fill="#3b82f6" className="cursor-pointer" onClick={() => setSelectedNetworkUser('Sarah Jenkins (1st)')} />
                    <text x="150" y="60" textAnchor="middle" fill="#60a5fa" fontSize="10">Sarah Jenkins</text>

                    <circle cx="450" cy="80" r="12" fill="#3b82f6" className="cursor-pointer" onClick={() => setSelectedNetworkUser('Alex Mercer (1st)')} />
                    <text x="450" y="60" textAnchor="middle" fill="#60a5fa" fontSize="10">Alex Mercer</text>

                    <circle cx="300" cy="250" r="12" fill="#3b82f6" className="cursor-pointer" onClick={() => setSelectedNetworkUser('David Chen (1st)')} />
                    <text x="300" y="275" textAnchor="middle" fill="#60a5fa" fontSize="10">David Chen</text>

                    {/* 2nd Degree Nodes */}
                    <circle cx="70" cy="150" r="10" fill="#4b5563" className="cursor-pointer" onClick={() => setSelectedNetworkUser('Elon Musk (2nd degree suggestion)')} />
                    <text x="70" y="175" textAnchor="middle" fill="#9ca3af" fontSize="10">Elon Musk</text>

                    <circle cx="530" cy="150" r="10" fill="#4b5563" className="cursor-pointer" onClick={() => setSelectedNetworkUser('Satya Nadella (2nd degree suggestion)')} />
                    <text x="530" y="175" textAnchor="middle" fill="#9ca3af" fontSize="10">Satya Nadella</text>
                  </svg>

                  {selectedNetworkUser && (
                    <div className="absolute bottom-4 left-4 right-4 bg-gray-900 border border-gray-800 p-4 rounded-xl text-xs flex justify-between items-center shadow-lg">
                      <span>Node Selected: <strong>{selectedNetworkUser}</strong></span>
                      <button onClick={() => setSelectedNetworkUser(null)} className="text-gray-400 hover:text-white font-bold">Close</button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Tab 4: Jobs Portal */}
          {activeTab === 'jobs' && (
            <div className="space-y-6">
              
              {/* Job recommendations banner */}
              <div className="bg-gradient-to-r from-blue-900/30 to-indigo-900/30 border border-blue-800/40 rounded-3xl p-6 relative overflow-hidden">
                <div className="relative z-10 space-y-2">
                  <h3 className="font-extrabold text-lg text-blue-300 font-outfit">AI Recommended Jobs for You</h3>
                  <p className="text-gray-300 text-xs">Our AI-matching engine scores positions by matching requirements with the skills endorsements listed on your profile.</p>
                </div>
              </div>

              {/* Job Listings */}
              <div className="space-y-4">
                {jobs.map((job) => {
                  const score = getJobRecomScore(job.skillsRequired);
                  return (
                    <div key={job.id} className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-6">
                      <div className="flex gap-4 items-start">
                        <div className="w-14 h-14 bg-gray-900 border border-gray-800 rounded-2xl overflow-hidden flex items-center justify-center shrink-0">
                          <img src={job.logo} alt="Company Logo" className="w-full h-full object-cover" />
                        </div>
                        <div className="space-y-1">
                          <div className="flex items-center gap-3">
                            <h4 className="font-bold text-white text-base leading-snug">{job.title}</h4>
                            <span className={`px-2 py-0.5 text-xxs font-bold rounded ${score > 70 ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-amber-500/10 text-amber-400 border border-amber-500/20'}`}>
                              {score}% Match
                            </span>
                          </div>
                          <p className="text-gray-400 text-xs">{job.company} • {job.location}</p>
                          <div className="flex flex-wrap gap-2 pt-2">
                            {job.skillsRequired.map((s, idx) => (
                              <span key={idx} className="bg-gray-900 border border-gray-800 text-gray-400 px-2 py-1 rounded text-xxs">
                                {s}
                              </span>
                            ))}
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center gap-3 border-t md:border-t-0 pt-4 md:pt-0 border-gray-800">
                        <button
                          onClick={() => {
                            setJobs(jobs.map(j => j.id === job.id ? { ...j, saved: !j.saved } : j));
                          }}
                          className="p-2.5 bg-gray-900 hover:bg-gray-800 text-gray-300 border border-gray-800 rounded-xl transition-all"
                        >
                          <Bookmark className={`h-5 w-5 ${job.saved ? 'fill-blue-500 text-blue-400' : ''}`} />
                        </button>
                        <button
                          onClick={() => {
                            setJobs(jobs.map(j => j.id === job.id ? { ...j, applied: true } : j));
                          }}
                          disabled={job.applied}
                          className={`px-5 py-2.5 rounded-xl text-xs font-semibold transition-all ${job.applied ? 'bg-emerald-600/20 text-emerald-400 border border-emerald-500/20 cursor-default' : 'bg-blue-600 hover:bg-blue-700 text-white shadow-lg shadow-blue-500/20'}`}
                        >
                          {job.applied ? 'Applied' : 'Apply Now'}
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Tab 5: Notifications */}
          {activeTab === 'notifications' && (
            <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-6">
              <div className="flex justify-between items-center">
                <h3 className="font-bold text-lg text-white font-outfit">Recent Alerts</h3>
                <button 
                  onClick={() => setNotifications(notifications.map(n => ({ ...n, read: true })))}
                  className="text-xs text-blue-400 hover:underline font-semibold"
                >
                  Mark all as read
                </button>
              </div>

              <div className="space-y-4">
                {notifications.map((n) => (
                  <div 
                    key={n.id} 
                    className={`p-4 rounded-2xl border flex items-start gap-4 transition-all duration-200 ${n.read ? 'bg-gray-900/10 border-gray-900' : 'bg-blue-600/5 border-blue-900/40'}`}
                  >
                    <div className="p-2.5 bg-blue-600/10 text-blue-400 rounded-xl">
                      {n.type === 'CONNECTION_REQUEST' && <Users className="h-5 w-5" />}
                      {n.type === 'POST_LIKE' && <ThumbsUp className="h-5 w-5" />}
                      {n.type === 'PROFILE_VIEW' && <UserIcon className="h-5 w-5" />}
                    </div>
                    
                    <div className="space-y-1">
                      <p className="text-gray-200 text-xs">
                        <strong className="text-white">{n.actor}</strong> {n.message}
                      </p>
                      <span className="text-gray-500 text-xxs">{n.time}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tab 6: Search Console */}
          {activeTab === 'search' && (
            <div className="space-y-6">
              <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl">
                <h3 className="font-bold text-lg text-white font-outfit mb-2">Search results for "{searchQuery}"</h3>
                <p className="text-gray-400 text-xs">Elasticsearch indexes successfully matched the following results in under 92ms.</p>
              </div>

              {/* Matched Users */}
              {searchResults.users.length > 0 && (
                <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4">
                  <h4 className="font-bold text-sm text-gray-300 uppercase tracking-wider">People</h4>
                  <div className="space-y-4">
                    {searchResults.users.map((u, idx) => (
                      <div key={idx} className="flex justify-between items-center py-2 border-b border-gray-900 last:border-0">
                        <div className="flex gap-3 items-center">
                          <div className="w-10 h-10 bg-gray-800 rounded-full flex items-center justify-center text-white font-bold text-xs uppercase">
                            {u.name.substring(0,2)}
                          </div>
                          <div>
                            <h5 className="font-bold text-white text-sm">{u.name}</h5>
                            <p className="text-gray-400 text-xs">{u.headline} • {u.location}</p>
                          </div>
                        </div>
                        <button className="px-4 py-1.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-semibold">
                          Connect
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Matched Jobs */}
              {searchResults.jobs.length > 0 && (
                <div className="bg-[#0c1322] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4">
                  <h4 className="font-bold text-sm text-gray-300 uppercase tracking-wider">Jobs</h4>
                  <div className="space-y-4">
                    {searchResults.jobs.map((job) => (
                      <div key={job.id} className="flex justify-between items-center py-2">
                        <div>
                          <h5 className="font-bold text-white text-sm">{job.title}</h5>
                          <p className="text-gray-400 text-xs">{job.company} • {job.location}</p>
                        </div>
                        <button className="px-4 py-1.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-semibold">
                          Apply
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {searchResults.users.length === 0 && searchResults.jobs.length === 0 && (
                <div className="text-center py-12 text-gray-400">
                  No matches found for query "{searchQuery}".
                </div>
              )}
            </div>
          )}

        </section>
      </main>
    </div>
  );
}
